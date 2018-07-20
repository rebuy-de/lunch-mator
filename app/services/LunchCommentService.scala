package services

import scala.concurrent.{ExecutionContext, Future}

import play.api.db.slick.DatabaseConfigProvider

import com.google.inject.Inject
import mappers.LunchMessageMapper
import models.{LunchComment, LunchRow, UserRow}
import persistence.repository.{LunchComments, LunchTableRows}

class LunchCommentService @Inject()(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider) extends DbService {

  def getCommentsForLunch(lunchId: Int): Future[Seq[LunchComment]] = usingDB {
    LunchComments.getMessagesForLunch(lunchId)
  }

  def postComment(messageText: String, lunchId: Int)(implicit user: UserRow): Future[LunchRow] = usingDB {
    val message = LunchMessageMapper.map(messageText, lunchId, user.id.get)
    LunchComments.postNewMessage(message).flatMap(_ => LunchTableRows.getLunchWithId(lunchId))
  }
}
