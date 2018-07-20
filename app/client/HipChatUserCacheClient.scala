package client

import com.google.inject.Inject
import com.typesafe.config.Config
import models.Formats._
import models.{HipChatUser, Page}
import play.api.Logger
import play.api.cache.CacheApi
import play.cache.NamedCache
import play.mvc.Http
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HipChatUserCacheClient @Inject()(configuration: Config,
                                       @NamedCache("hipchat-user-cache") cache: CacheApi,
                                       restClientWrapper: RestClientWrapper)
                                      (implicit executionContext: ExecutionContext) {

  private val apiBaseUrl = configuration.getString("hipchat.api.baseurl")
  private val readToken = configuration.getString("hipchat.api.read.accesstoken")

  def getAllUsers: Future[Seq[HipChatUser]] = {
    val url = s"$apiBaseUrl/user?max-results=50"
    cache.getOrElse("hipchat-users", 12.hours) {
      getUsers(url)
    }
  }

  private def getUsers(link: String): Future[Seq[HipChatUser]] = {
    val headers = List(
      Http.HeaderNames.AUTHORIZATION -> s"Bearer $readToken",
      Http.HeaderNames.ACCEPT_CHARSET -> "UTF-8"
    )
    Logger.info(s"Requesting HipChat for userList | URL:[$link]")
    restClientWrapper.get[Page[HipChatUser]](link, headers).flatMap { page =>
      if (page.links.next.isEmpty) {
        Future.successful(page.items)
      } else {
        getUsers(page.links.next.get).map { users =>
          users ++ page.items
        }
      }
    }
  }
}
