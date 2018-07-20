package controllers

import actors.messages.Message
import com.google.inject.Inject
import play.api.mvc.{Action, AnyContent, InjectedController}
import scala.concurrent.ExecutionContext
import services.{LunchService, MessageService}

class DebugController @Inject()(lunchService: LunchService, scheduler: MessageService)(implicit ec: ExecutionContext) extends InjectedController {
  def rescheduleReminder(lunchId: Int): Action[AnyContent] = Action.async {
    lunchService.getLunchAndRestaurant(lunchId).map { a =>
      scheduler.scheduleMessage(Message.LunchReminderMessage(lunchId), a._1.startTime.minusMinutes(10))
      Ok
    }
  }
}
