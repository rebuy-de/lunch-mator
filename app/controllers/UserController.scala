package controllers

import com.google.inject.Inject
import models.Formats._
import models.UserRow
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc.{Action, EssentialAction}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import services.UserService

class UserController @Inject()(userService: UserService)(implicit db: DatabaseConfigProvider) extends AuthenticatedController {

  def getAllUsers: EssentialAction = async {
    request =>
      userService.getAllUsers.map(restaurants =>
        Ok(Json.toJson(restaurants))
      )
  }

  def getUser(id: Int): EssentialAction = async { request =>
    userService.getUserById(id).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def createUser(): Action[JsValue] = async(parse.json) { request =>
    val user = request.body.as[UserRow]
    userService.addOrUpdateUser(user).map {
      result => Created
    }.recoverWith {
      case e: Exception => Future(InternalServerError("Already exists " + e.getMessage))
    }
  }
}
