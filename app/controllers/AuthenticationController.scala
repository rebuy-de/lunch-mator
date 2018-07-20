package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationController @Inject()(implicit db: DatabaseConfigProvider, ec: ExecutionContext) extends AuthenticatedController {

  def authenticated: Action[AnyContent] = Action.async { request =>
    validateSession(request) map {
      case Some(_) => Ok
      case _ => Unauthorized
    }
  }

  def logOut(): Action[AnyContent] = Action.async { request =>
    Logger.info(s"Log Out:[${request.session.get("email").getOrElse("NO USER")}]")
    Future.successful(Redirect("/login").withNewSession)
  }
}

