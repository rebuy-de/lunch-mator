package services

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import com.google.inject.name.Named
import org.joda.time.{DateTime, Seconds}
import play.api.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

class MessageService @Inject()(system: ActorSystem, @Named("lunch-mator-actor") actorSystem: ActorRef)(implicit ec: ExecutionContext) {

  def publishMessage(message: Any): Unit = {
    actorSystem ! message
  }

  def scheduleMessage(message: Any, deliveryTime: DateTime): Unit = {
    val now = DateTime.now()
    val delayInterval = Seconds.secondsBetween(now, deliveryTime).getSeconds

    if (delayInterval > 0) {
      Logger.info(s"Message scheduled in $delayInterval seconds")
      system.scheduler.scheduleOnce(delayInterval.seconds, actorSystem, message)
    }
  }
}
