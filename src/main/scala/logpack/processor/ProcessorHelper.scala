package logpack.processor

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.circe.Json

import scala.annotation.tailrec
import scala.util.Try

object ProcessorHelper {

  val formats = List(
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    DateTimeFormatter.ISO_DATE_TIME,
    DateTimeFormatter.ISO_LOCAL_DATE_TIME
  )

  def merge(attrs1: Json, attrs2: Json): Json = (attrs1, attrs2) match {
    case (Json.Null, Json.Null) => Json.Null
    case (Json.Null, _)         => attrs2
    case (_, Json.Null)         => attrs1
    case (_, _)                 => attrs1.deepMerge(attrs2)
  }

  @tailrec
  def tryFind(field: String, json: Json): Option[Json] =
    field.split("\\.").toList match {
      case Nil      => None
      case h :: Nil => json.asObject.map(_.toMap).getOrElse(Map.empty).get(h)
      case h :: t =>
        tryFind(
          t.mkString("."),
          json.asObject.map(_.toMap).getOrElse(Map.empty).getOrElse(h, Json.Null)
        )
    }

  def createJson(field: String, value: Json): Json =
    field.split("\\.").toList match {
      case Nil      => Json.Null
      case h :: Nil => Json.obj((h, value))
      case h :: t   => Json.obj((h, createJson(t.mkString("."), value)))
    }

  def toDate(str: String): Option[LocalDateTime] =
    formats.view
      .map { format =>
        Try(LocalDateTime.parse(str, format)).toOption
      }
      .find(_.isDefined)
      .flatten

  def toDate(str: String, format: String): Option[LocalDateTime] =
    Try(LocalDateTime.parse(str, DateTimeFormatter.ofPattern(format))).toOption
}
