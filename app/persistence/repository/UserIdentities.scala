package persistence.repository

import models.UserIdentityRow
import slick.jdbc.PostgresProfile.api._

class UserIdentities(tag: Tag) extends Table[UserIdentityRow](tag, Some("lunch_world"), "user_identity") {

  def userEmail = column[String]("user_email", O.PrimaryKey)

  def password = column[String]("encrypted_password")

  def salt = column[String]("salt")

  override def * = (userEmail, password, salt) <> (UserIdentityRow.tupled, UserIdentityRow.unapply _)
}

object UserIdentities {

  val userIdentities = TableQuery[UserIdentities]

  def getUserIdentity(email: String) = {
    userIdentities.filter(_.userEmail === email).result
  }

  def createNewUser(userIdentity: UserIdentityRow) = {
    (userIdentities returning userIdentities) += userIdentity
  }
}
