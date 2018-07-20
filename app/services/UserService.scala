package services

import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider

import com.google.inject.Inject
import models.UserRow
import persistence.repository.Users

class UserService @Inject()(implicit val dbConfigDataProvider: DatabaseConfigProvider) extends DbService {

  def getUserById(userId: Int): Future[UserRow] = usingDB {
    Users.findById(userId)
  }

  def addOrUpdateUser(user: UserRow): Future[Int] = usingDB {
    Users.addOrUpdateUser(user)
  }

  def findUserWithEmail(email: String): Future[Option[UserRow]] = usingDB {
    Users.findUserByEmail(email)
  }

  def getAllUsers: Future[Seq[UserRow]] = usingDB {
    Users.getAll
  }

  def validateUser(email: String): Future[Boolean] = usingDB {
    Users.isPresent(email)
  }
}
