package services

import com.google.inject.Inject
import exceptions.AuthenticationException
import models.UserRow
import persistence.repository.Users
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedController @Inject()(implicit db: DatabaseConfigProvider, ec: ExecutionContext) extends InjectedController with DbService {

  type ControllerBlock[A] = AuthenticatedRequest[A] => Future[Result]

  def async[A](block: ControllerBlock[AnyContent]): EssentialAction = async(parse.anyContent)(block)

  def async[A](bp: BodyParser[A])(block: ControllerBlock[A]): Action[A] = Action.async(bp) {
    request =>
      validateSession(request).flatMap { validUser =>
        if (validUser.isDefined) {
          block(AuthenticatedRequest(validUser.get, request))
        }
        else {
          throw new AuthenticationException(origin = request.path)
        }
      }
  }

  def validateSession(request: Request[Any]): Future[Option[UserRow]] = {
    if (request.session.get("email").isEmpty) {
      return Future.successful(None)
    }

    validateUser(request.session.get("email").get)
  }

  private def validateUser(email: String): Future[Option[UserRow]] = usingDB {
    Users.getByEmail(email)
  }
}

case class AuthenticatedRequest[A](userRow: UserRow, request: Request[A]) extends WrappedRequest[A](request)
