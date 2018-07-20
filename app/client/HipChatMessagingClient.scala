package client

import com.google.inject.Inject
import com.typesafe.config.Config
import mappers.HipChatMapper
import models.Formats._
import models.{HipChatMessage, HipChatPing}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http
import scala.concurrent.{ExecutionContext, Future}
import services.LunchService

class HipChatMessagingClient @Inject()(configuration: Config, lunchService: LunchService, restClient: RestClientWrapper)(implicit executionContext: ExecutionContext) {
  private val apiBaseUrl = configuration.getString("hipchat.api.baseurl")
  private val writeToken = configuration.getString("hipchat.lunchmator.write.accesstoken")
  private val lunchMatorRoomId = configuration.getString("hipchat.lunchmator.chatroom.id")

  def sendMessage(users: Seq[HipChatPing], hipchatMessage: HipChatMessage): Future[String] = {
    val message = HipChatMapper.mapNotificationMessage(users, hipchatMessage)
    val url = s"$apiBaseUrl/room/$lunchMatorRoomId/notification"
    val headers = List(
      Http.HeaderNames.CONTENT_TYPE -> Http.MimeTypes.JSON,
      Http.HeaderNames.AUTHORIZATION -> s"Bearer $writeToken"
    )

    Logger.info(s"Sending message to hipChat URL:[$url] | Message:[$message]")
    restClient.post[JsValue, String](url, headers, Json.toJson(message)).map {
      case Some(result) => result
      case None => ""
    }
  }
}

