package logpack.processor

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

  @tailrec
  def tryFind(field: String, binding: Map[String, Any]): Option[Any] =
    field.split("\\.").toList match {
      case Nil      => None
      case h :: Nil => binding.get(h)
      case h :: t =>
        tryFind(
          t.mkString("."),
          binding.getOrElse(h, Map.empty).asInstanceOf[Map[String, Any]]
        )
    }

  @tailrec
  def find(field: String, binding: Map[String, Any], default: String = ""): String =
    field.split("\\.").toList match {
      case Nil      => default
      case h :: Nil => binding.get(h).map(_.toString).getOrElse("")
      case h :: t =>
        find(
          t.mkString("."),
          binding.getOrElse(h, Map.empty).asInstanceOf[Map[String, Any]]
        )
    }

  def createMap(field: String, value: Any): Map[String, Any] =
    field.split("\\.").toList match {
      case Nil      => Map.empty
      case h :: Nil => Map(h -> value)
      case h :: t   => Map(h -> createMap(t.mkString("."), value))
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
