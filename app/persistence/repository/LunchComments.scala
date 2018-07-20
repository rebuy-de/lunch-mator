package persistence.repository

import com.github.tototoshi.slick.PostgresJodaSupport._
import models.LunchComment
import org.joda.time.DateTime
import slick.dbio.Effect.{Read, Write}
import slick.jdbc.PostgresProfile.api._
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction}

class LunchComments(tag: Tag) extends Table[LunchComment](tag, Some("lunch_world"), "lunch_comment") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)

  def lunchId = column[Int]("lunch_id")

  def authorId = column[Int]("author_id")

  def replyTo = column[Int]("reply_to")

  def createdAt = column[DateTime]("created_at")

  def message = column[String]("comment")

  override def * = (id.?, lunchId, authorId, replyTo.?, createdAt.?, message) <> (LunchComment.tupled, LunchComment.unapply _)
}

object LunchComments {

  lazy private val chatMessages = TableQuery[LunchComments]

  def getMessagesForLunch(lunchId: Int): FixedSqlStreamingAction[Seq[LunchComment], LunchComment, Read] = {
    chatMessages.filter(_.lunchId === lunchId).sortBy(_.createdAt.asc).result
  }

  def getMessage(lunchId: Int, authorId: Int): FixedSqlStreamingAction[Seq[LunchComment], LunchComment, Read] = {
    chatMessages.filter(_.id === lunchId).filter(_.authorId === authorId).sortBy(_.createdAt.asc).result
  }

  def postNewMessage(message: LunchComment): FixedSqlAction[Int, NoStream, Write] = {
    chatMessages += message
  }
}
