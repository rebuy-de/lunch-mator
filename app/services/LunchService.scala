package services

import actors.messages.Message.{LunchReminderMessage, NewLunchCreatedMessage}
import com.google.inject.Inject
import com.typesafe.config.Config
import mappers.LunchMapper
import models._
import org.joda.time.DateTime
import persistence.repository.LunchTableRows
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class LunchService @Inject()(scheduler: MessageService, configuration: Config)(implicit ec: ExecutionContext, implicit val dbConfigDataProvider: DatabaseConfigProvider) extends DbService {
  private val lunchMatorHost = configuration.getString("lunchmator.host")

  def getLunchWithOpenSpotsAfter(user: UserRow): Future[Vector[(LunchRow, RestaurantRow, Int, Int)]] = usingDB {
    LunchTableRows.getLunchWithOpenSpotsAfter(user.email, new DateTime().withDurationAdded(30 * 60 * 1000, -1))
  }

  def getAllActiveLunch: Future[Seq[LunchDetail]] = usingDB {
    LunchTableRows.getLunchAfter(DateTime.now())
  }

  def getLunchForUserNotPast(email: String): Future[Vector[(LunchRow, RestaurantRow)]] = usingDB {
    LunchTableRows.getLunchForUserAfter(email, new DateTime().withDurationAdded(30 * 60 * 1000, -1))
  }

  def getLunchDetail(email: String, lunchId: Int): Future[Vector[(LunchRow, RestaurantRow, Int, Int)]] = usingDB {
    LunchTableRows.getLunchWithRestaurant(email, lunchId)
  }

  def getLunchAndRestaurant(id: Int): Future[(LunchRow, RestaurantRow)] = usingDB {
    LunchTableRows.getLunchDetailsWithId(id)
  }

  def getLunch(id: Int): Future[LunchRow] = usingDB {
    LunchTableRows.getLunchWithId(id)
  }

  def deactivateLunch(lunchId: Int): Future[Int] = usingDB {
    LunchTableRows.deactivateLunch(lunchId)
  }

  def createLunch(user: UserRow, lunchDto: CreateLunchDto): Future[Int] = usingDB {
    val lunch = LunchMapper.map(lunchDto)
    LunchTableRows.createLunch(lunch).map(lunchId => {
      Logger.info(s"User:[${user.firstName} ${user.lastName}] | RestaurantId:[${lunchDto.restaurantId}] | New lunch created ")
      scheduler.publishMessage(NewLunchCreatedMessage(lunchId, user))
      val remindAt = new DateTime(lunchDto.startTime).minusMinutes(10)
      scheduler.scheduleMessage(LunchReminderMessage(lunchId), remindAt)
      lunchId
    })
  }

  def validateLunchCreation(lunch: LunchRow): ValidationResult = {
    var errors = new ListBuffer[String]()

    if (lunch.startTime.isBefore(DateTime.now)) {
      errors += "Cannot start in the past"
    }

    if (lunch.startTime.isAfter(DateTime.now.plusDays(60))) {
      errors += "Cannot schedule a lunch more than 2 months in the future"
    }

    if (lunch.maxSize < 3) {
      errors += "Cannot have max size less than 3"
    }

    if (lunch.lunchName.size > 50) {
      errors += "Cannot name of more than 50 chars"
    }

    ValidationResult(errors.toList)
  }

  def getLunchUrl(lunchId: Int): String = {
    s"$lunchMatorHost/s/lunch/$lunchId"
  }
}
