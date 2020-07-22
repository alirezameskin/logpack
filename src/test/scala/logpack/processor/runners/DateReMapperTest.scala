package logpack.processor.runners

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.circe.Json
import io.circe.syntax._
import logpack.config.DateReMapper
import logpack.{Info, LogRecord}

class DateReMapperTest extends org.scalatest.FunSuite {
  def now = new Timestamp(System.currentTimeMillis()).getTime

  test("Should parse ISO date format") {
    val time      = LocalDateTime.of(2020, 1, 15, 12, 23, 49)
    val processor = DateReMapper(List("custom_date_field"))
    val attributes =
      Json.obj(
        ("custom_date_field", time.format(DateTimeFormatter.ISO_DATE_TIME).asJson),
        ("field2", "2020-02-17T20:34:33Z".asJson)
      )
    val record = LogRecord("Log Message", Some(Info), Some(now), attributes)

    val runner = new DateReMapperRunner
    val result = runner.run(processor, record)

    assert(result.time == Some(Timestamp.valueOf(time).getTime))
  }

  test("Between multiple fields it should select the first possible value") {
    val time1     = LocalDateTime.of(2020, 1, 15, 12, 23, 49)
    val time2     = LocalDateTime.of(2019, 1, 15, 12, 23, 49)
    val processor = DateReMapper(List("field1", "field2", "field3", "field4"))
    val attributes =
      Json.obj(
        "filed1" -> "Invalid time value1".asJson,
        "field2" -> "Invalid time value2".asJson,
        "field3" -> time1.format(DateTimeFormatter.ISO_DATE_TIME).asJson,
        "field4" -> time2.format(DateTimeFormatter.ISO_DATE_TIME).asJson
      )

    val record = LogRecord("Log Message", Some(Info), Some(now), attributes = attributes)
    val runner = new DateReMapperRunner
    val result = runner.run(processor, record)

    assert(result.time == Some(Timestamp.valueOf(time1).getTime))
  }

  test("Should be able to select in nested Map") {
    val time1 = LocalDateTime.of(2020, 1, 15, 12, 23, 49)
    val time2 = LocalDateTime.of(2019, 1, 15, 12, 23, 49)

    val processor = DateReMapper(List("field1", "field2", "field3.date", "field4"))
    val attributes =
      Json.obj(
        "filed1" -> "Invalid time value1".asJson,
        "field2" -> "Invalid time value2".asJson,
        "field3" -> Json.obj("date" -> time1.format(DateTimeFormatter.ISO_DATE_TIME).asJson),
        "field4" -> time2.format(DateTimeFormatter.ISO_DATE_TIME).asJson
      )

    val record = LogRecord("Log Message", Some(Info), Some(now), attributes = attributes)
    val runner = new DateReMapperRunner
    val result = runner.run(processor, record)

    assert(result.time == Some(Timestamp.valueOf(time1).getTime))
  }
}
