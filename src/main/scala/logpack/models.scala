package logpack

import cats.Show
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}

sealed trait Level
case object Emergency extends Level
case object Alert     extends Level
case object Critical  extends Level
case object Error     extends Level
case object Warning   extends Level
case object Notice    extends Level
case object Info      extends Level
case object Debug     extends Level

final case class LogRecord(
  message: String,
  level: Option[Level] = None,
  time: Option[Long] = None,
  attributes: Json = Json.Null
)

object Level {
  implicit val jsonEncoder = new Encoder[Level] {
    override def apply(a: Level): Json = a.toString.asJson
  }
}

object LogRecord {
  val ONE_LINE_PRINT: Show[LogRecord] = (rec: LogRecord) => rec.asJson.noSpaces + "\n"
  val PRETTY_PRINT: Show[LogRecord]   = (rec: LogRecord) => rec.asJson.spaces2 + "\n"
}

final case class UrlDetails(
  scheme: Option[String],
  host: Option[String],
  port: Option[Int],
  path: Option[String],
  query: Option[String],
  fragment: Option[String]
)

final case class UserAgent(
  deviceClass: Option[String],
  os: Option[String],
  browserFamily: Option[String],
  version: Option[String]
)
