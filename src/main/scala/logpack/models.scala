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
  attributes: Map[String, Any] = Map.empty
)

object LogRecord {
  val ONE_LINE_PRINT: Show[LogRecord] = (rec: LogRecord) => rec.asJson.noSpaces + "\n"
  val PRETTY_PRINT: Show[LogRecord]   = (rec: LogRecord) => rec.asJson.spaces2 + "\n"

  def toJson(obj: Any): Json = obj match {
    case x: Int        => x.asJson
    case x: String     => x.asJson
    case x: UrlDetails => x.asJson
    case x: UserAgent  => x.asJson
    case x: Map[String, Any] =>
      Json.obj(x.map(r => (r._1, toJson(r._2))).toList: _*)
    case None => Json.Null
  }

  implicit val encoder: Encoder[LogRecord] = new Encoder[LogRecord] {
    override def apply(a: LogRecord): Json =
      Json.obj(
        ("message", a.message.asJson),
        ("time", a.time.asJson),
        ("level", a.level.map(_.toString).asJson),
        ("attributes", toJson(a.attributes))
      )
  }
}

final case class UrlDetails(
  scheme: Option[String],
  host: Option[String],
  port: Option[Int],
  path: Option[String],
  fragment: Option[String]
)

final case class UserAgent(deviceClass: String, os: String, browserFamily: String, version: String)
