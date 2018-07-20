package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Action, AnyContent, InjectedController}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import services.GoogleAuthorizationService

class GoogleAuthenticationController @Inject()(googleAuthorizationService: GoogleAuthorizationService) extends InjectedController with AuthorizationController {

  def authorize(): Action[AnyContent] = Action.async { request =>
    val params = request.queryString.map { case (k, v) => k -> v.mkString }
    val startPage = params.getOrElse("state", "/welcome")
    val googleUser = googleAuthorizationService.googleAuthorize(params)
    googleUser.map { user =>
      Logger.info(s"Login Success: [${user.name}]")
      Redirect(startPage).withSession("email" -> user.email)
    }
  }

  def redirectToGoogle(): Action[AnyContent] = Action.async { request =>
    val params = request.queryString.map { case (k, v) => k -> v.mkString }
    val origin = params.getOrElse("origin", "/welcome")
    Future.successful(Redirect(googleAuthorizationService.getGoogleSignInPage(origin = origin)))
  }
}
