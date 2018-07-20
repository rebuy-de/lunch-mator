package controllers

import scala.concurrent.ExecutionContext

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{Action, Controller, EssentialAction}

import actors.messages.Message.NewCommentMessage
import com.google.inject.Inject
import models.NewComment
import models.Formats._
import services.{AuthenticatedController, LunchCommentService, MessageService}

class CommentController @Inject()(chatMessageService: LunchCommentService, messageService: MessageService)(implicit ec: ExecutionContext, db: DatabaseConfigProvider) extends AuthenticatedController {

  def getComments(lunchId: Int): EssentialAction = async { request =>
    chatMessageService.getCommentsForLunch(lunchId).map { messages =>
      Ok(Json.toJson(messages))
    }
  }

  def postComment(lunchId: Int): Action[JsValue] = async(parse.json) { request =>
    implicit val user = request.userRow

    val message = request.body.as[NewComment]

    chatMessageService.postComment(message.text, lunchId).map { lunchRow =>
      if (lunchRow.active) {
        messageService.publishMessage(NewCommentMessage(lunchId, s"${user.firstName} ${user.lastName}", message.text))

        Ok
      }
      else {
        Locked
      }
    }
  }
}
