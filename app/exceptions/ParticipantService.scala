package exceptions

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider

import actors.messages.Message
import models._
import org.joda.time.DateTime
import persistence.repository.Participants
import services._

class ParticipantService @Inject()(userService: UserService, lunchService: LunchService, scheduler: MessageService)(implicit db: DatabaseConfigProvider, ec: ExecutionContext) extends DbService
{

  def addUserToLunch(user: UserRow, lunchId: Int): Future[Int] =
  {
    val joined = DateTime.now()
    lunchService.getLunch(lunchId).flatMap { lunch =>
      if (lunch.active) {
        Logger.info(s"User:[${user.firstName}] | lunchId:[$lunchId] | Participant added")
        addParticipant(ParticipantRow(lunchId, user.id.get, joined))
      } else {
        Logger.warn(s"User:[${user.firstName}] | lunchId:[$lunchId] | Attempt to join inactive lunch")
        Future.successful(0)
      }
    }
  }

  def removeUserFromLunch(user: UserRow, lunchId: Int): Future[Int] = usingDB {
    Logger.info(s"User:[${user.firstName}] | LunchId:[$lunchId] | Removing user")
    Participants.deactivateParticipantForLunch(user.id.get, lunchId).map { update =>
      scheduler.publishMessage(Message.ParticipantLeftMessage(lunchId))
      update
    }
  }

  def deactivateEmptyLunch(lunchId: Int): Future[Int] =
  {
    getParticipationDetails(lunchId).flatMap { result =>
      if (result.forall(p => !p._1.active)) {
        Logger.info(s"LunchId:[$lunchId] | no active users, deactivating")
        lunchService.deactivateLunch(lunchId)
      }
      else {
        Future.successful(0)
      }
    }
  }

  def getParticipationDetails(lunchId: Int): Future[Seq[(ParticipantRow, UserRow, LunchRow, RestaurantRow)]] = usingDB {
    Participants.getParticipantsForLunch(lunchId)
  }

  private def addParticipant(participant: ParticipantRow): Future[Int] = usingDB {
    Participants.addParticipant(participant)
  }
}
