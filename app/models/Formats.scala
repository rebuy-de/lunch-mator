package models

import java.sql.Timestamp
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object Formats {
  private lazy val ISODateTimeFormatter = ISODateTimeFormat.dateTime.withZone(DateTimeZone.UTC)
  private lazy val ISODateTimeParser = ISODateTimeFormat.dateTimeParser

  implicit val dateTimeFormatter = new Format[DateTime] {
    def reads(j: JsValue) = JsSuccess(ISODateTimeParser.parseDateTime(j.as[String]))

    def writes(o: DateTime): JsValue = JsString(ISODateTimeFormatter.print(o))
  }
  implicit val timeStampReads: Reads[Timestamp] = (__ \ "startTime").read[Long].map { long => new Timestamp(long) }
  implicit val timeStampWrites: Writes[Timestamp] = (__ \ "startTime").write[Long].contramap { a: Timestamp => a.getTime }
  implicit val timeStampFormat: Format[Timestamp] = Format(timeStampReads, timeStampWrites)

  implicit val userFormat: OFormat[UserRow] = Json.format[UserRow]
  implicit val userIdentityFormat: OFormat[UserIdentity] = Json.format[UserIdentity]
  implicit val newUserDtoFormat: OFormat[NewUserDto] = Json.format[NewUserDto]
  implicit val userRest: OFormat[(UserRow, RestaurantRow)] = Json.format[(UserRow, RestaurantRow)]

  implicit val restaurantFormat: OFormat[Restaurant] = Json.format[Restaurant]
  implicit val restaurantDtoFormat: OFormat[RestaurantDto] = Json.format[RestaurantDto]
  implicit val createRestaurantDtoFormat: OFormat[CreateRestaurantDto] = Json.format[CreateRestaurantDto]

  implicit val restaurantRowFormat: OFormat[RestaurantRow] = Json.format[RestaurantRow]

  implicit val participantDto: OFormat[ParticipantDto] = Json.format[ParticipantDto]
  implicit val lunchFormat: OFormat[LunchDto] = Json.format[LunchDto]
  implicit val myLunchFormat: OFormat[MyLunchDto] = Json.format[MyLunchDto]
  implicit val lunchDetailFormat: OFormat[LunchDetailDto] = Json.format[LunchDetailDto]

  implicit val lunchDtoFormat: OFormat[CreateLunchDto] = Json.format[CreateLunchDto]
  implicit val errorFormat: OFormat[Error] = Json.format[Error]

  implicit val linksFormat: OFormat[Links] = Json.format[Links]

  implicit val hipChatUserFormat: OFormat[HipChatUser] = Json.format[HipChatUser]

  implicit val hipChatPingFormat: OFormat[HipChatPing] = Json.format[HipChatPing]

  implicit val hipChatMessageFormat: OFormat[HipChatMessage] = Json.format[HipChatMessage]

  implicit val hipChatMessageDtoFormat: OFormat[HipChatMessageDto] = Json.format[HipChatMessageDto]

  implicit val hipChatInvitationFormat: OFormat[HipChatCommunication] = Json.format[HipChatCommunication]

  implicit val chatMessage: OFormat[LunchComment] = Json.format[LunchComment]
  implicit val newMessage: OFormat[NewComment] = Json.format[NewComment]
}
