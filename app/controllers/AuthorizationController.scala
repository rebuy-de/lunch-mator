package controllers

import play.api.mvc.{Action, AnyContent}

trait AuthorizationController {

  def authorize(): Action[AnyContent]
}
