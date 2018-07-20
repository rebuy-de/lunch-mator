package services

import java.util.Base64

import scala.concurrent.{ExecutionContext, Future}

import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json

import client.GoogleAuthenticationClient
import com.google.inject.Inject
import exceptions.{GoogleAuthenticationException, InvalidDomainException}
import mappers.UserMapper
import models.GoogleModels._
import persistence.repository.OAuthUser

class GoogleAuthorizationService @Inject()(googleAuthenticationClient: GoogleAuthenticationClient, userService: UserService)(implicit val df: DatabaseConfigProvider, ec: ExecutionContext) extends DbService {

  def getGoogleSignInPage(origin: String = "/welcome"): String = {
    googleAuthenticationClient.getGoogleSignInPage(origin)
  }

  def getGoogleAuthorization(authorizationCode: String): Future[GoogleAuthorization] = {
    googleAuthenticationClient.getGoogleAuthorization(authorizationCode)
  }

  def googleAuthorize(params: Map[String, String]): Future[GoogleUserInformation] = {
    val authorizationCode = params.getOrElse("code", throw GoogleAuthenticationException("/"))

    val googleUserData = getGoogleAuthorization(authorizationCode)

    googleUserData.flatMap { googleAuthorization =>
      val googleUserInformation = decodeUserData(googleAuthorization)

      if (googleUserInformation.email.endsWith("rebuy.com")) {
        saveUser(googleUserInformation).map { userRow =>
          Logger.info(s"User:[$userRow] logged-in")
          googleUserInformation
        }
      }
      else {
        throw InvalidDomainException(googleUserInformation.email)
      }
    }
  }

  private def decodeUserData(googleAuthorization: GoogleAuthorization) = {
    val tokens = googleAuthorization.id_token.split("\\.")
    Logger.debug(s"Google response: [${googleAuthorization.toString}]")
    Logger.debug(s"Decoding: [${tokens(1)}]")
    Json.parse(Base64.getDecoder.decode(tokens(1).getBytes("UTF-8"))).as[GoogleUserInformation]
  }

  private def saveOAuth(googleUserInformation: GoogleUserInformation, googleAuthorization: GoogleAuthorization) = usingDB {
    OAuthUser.addNewUser(UserMapper.map(googleUserInformation, googleAuthorization))
  }

  private def saveUser(googleUserInformation: GoogleUserInformation) = {
    userService.addOrUpdateUser(UserMapper.map(googleUserInformation))
  }
}
