package persistence.repository

import models.OAuthUserRow
import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIOAction
import slick.dbio.Effect.{Read, Write}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import slick.sql.{FixedSqlStreamingAction, SqlAction}

class OAuthUser(tag: Tag) extends Table[OAuthUserRow](tag, Some("lunch_world"), "oauth_identity") {

  def email: Rep[String] = column[String]("user_email", O.PrimaryKey)

  def accessToken: Rep[String] = column[String]("access_token")

  def idToken: Rep[String] = column[String]("id_token")

  override def * : ProvenShape[OAuthUserRow] = (email, accessToken, idToken) <> (OAuthUserRow.tupled, OAuthUserRow.unapply)
}

object OAuthUser {

  private val oAuthUser = TableQuery[OAuthUser]

  def getByEmail(email: String): SqlAction[OAuthUserRow, NoStream, Read] = {
    oAuthUser.filter(_.email === email).result.head
  }

  def addNewUser(user: OAuthUserRow): DBIOAction[Int, NoStream, Read with Write] = {
    oAuthUser.filter(_.email === user.email).exists.result.flatMap { exists =>
      if (!exists) {
        oAuthUser += user
      } else {
        DBIO.successful(0)
      }
    }
  }

  def getAll: FixedSqlStreamingAction[Seq[OAuthUserRow], OAuthUserRow, Read] = {
    oAuthUser.result
  }
}
