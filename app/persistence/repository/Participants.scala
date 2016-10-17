package persistence.repository

import java.sql.Timestamp
import models.ParticipantRow
import play.api.Logger
import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

class Participants(tag: Tag) extends Table[ParticipantRow](tag, Some("lunch_world"), "participants") {

  def lunchId = column[Int]("lunch_table_id")

  def userId = column[Int]("user_id")

  def joinedAt = column[Timestamp]("joined_at")

  override def * = (lunchId, userId, joinedAt) <> (ParticipantRow.tupled, ParticipantRow.unapply _)
}

object Participants {

  lazy val participants = TableQuery[Participants]

  def addParticipant(participantRow: ParticipantRow) = {
    Logger.info(s"Inserting $participantRow into table")
    participants += participantRow
  }

  def getParticipantsForLunch(lunchId: Int) = {
    participants.filter(_.lunchId === lunchId).result
  }
}
