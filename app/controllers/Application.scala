package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent, EssentialAction}
import scala.concurrent.{ExecutionContext, Future}
import services.UserService

class Application @Inject()(userService: UserService, assets: Assets)(implicit db: DatabaseConfigProvider, ec: ExecutionContext) extends AuthenticatedController {
  val uiResourceRoot = "/public/ui/"

  def secured(): EssentialAction = async {
    request =>
      assets.at(uiResourceRoot, "index.html").apply(request)
  }

  def securedWithParam(id: Any): EssentialAction = async {
    request =>
      Logger.info(s"Secured route:[$id] requested")
      assets.at(uiResourceRoot, "index.html").apply(request)
  }

  def unSecure(): Action[AnyContent] = Action.async { request =>
    val params = request.queryString.map { case (k, v) => k -> v.mkString }
    val origin = params.getOrElse("origin", "/welcome")
    if (request.session.get("email").isDefined) {
      userService.validateUser(request.session.get("email").get).flatMap { valid =>
        if (valid) {
          Future.successful(Redirect("/welcome"))
        } else {
          assets.at(uiResourceRoot, "index.html").apply(request)
        }
      }
    } else {
      assets.at(uiResourceRoot, "index.html").apply(request)
    }
  }
}
