package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Action, AnyContent, InjectedController}
import scala.concurrent.Future

class AuthenticationController @Inject() extends InjectedController {

  def logOut(): Action[AnyContent] = Action.async { request =>
    Logger.info(s"Log Out:[${request.session.get("email").getOrElse("NO USER")}]")
    Future.successful(Redirect("/login").withNewSession)
  }
}

