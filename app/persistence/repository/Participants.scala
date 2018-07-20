package persistence.repository

import com.github.tototoshi.slick.PostgresJodaSupport._
import models.{LunchRow, ParticipantRow, RestaurantRow, UserRow}
import org.joda.time.DateTime
import play.api.Logger
import slick.dbio.Effect.{Read, Write}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction, SqlAction}

class Participants(tag: Tag) extends Table[ParticipantRow](tag, Some("lunch_world"), "participants") {

  def lunchId = column[Int]("lunch_table_id")

  def userId = column[Int]("user_id")

  def active = column[Boolean]("active")

  def joinedAt = column[DateTime]("joined_at")

  def pk = primaryKey("pk_participants", (lunchId, userId))

  override def * = (lunchId, userId, joinedAt, active) <> (ParticipantRow.tupled, ParticipantRow.unapply _)
}

object Participants {

  lazy val participants = TableQuery[Participants]

  def addParticipant(participantRow: ParticipantRow): SqlAction[Int, NoStream, Effect] = {
    Logger.debug(s"Inserting $participantRow into table")
    sqlu"""INSERT INTO lunch_world.participants (lunch_table_id, user_id, joined_at)
            VALUES (${participantRow.lunchId},${participantRow.userId},${participantRow.joined})
           ON CONFLICT(user_id, lunch_table_id) DO UPDATE SET active = TRUE;
      """
  }

  def getParticipantsForLunch(lunchId: Int): FixedSqlStreamingAction[Seq[(ParticipantRow, UserRow, LunchRow, RestaurantRow)], (ParticipantRow, UserRow, LunchRow, RestaurantRow), Read] = {
    val q = for {
      lunch <- LunchTableRows.lunchTableRows.filter(_.id === lunchId)
      restaurant <- Restaurants.restaurants.filter(_.id === lunch.restaurantId)
      (participants, user) <- participants.filter(_.lunchId === lunchId).filter(_.active) join Users.users on (_.userId === _.id)
    } yield {
      (participants, user, lunch, restaurant)
    }
    q.sortBy(_._1.joinedAt.asc).result
  }

  def deactivateParticipantForLunch(userId: Int, lunchId: Int): FixedSqlAction[Int, NoStream, Write] = {
    val q = for {p <- participants if p.userId === userId && p.lunchId === lunchId} yield {
      p.active
    }
    q.update(false)
  }
}
