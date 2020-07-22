package logpack.processor.runners

import java.sql.Timestamp

import io.circe.Json
import io.circe.syntax._
import logpack._
import logpack.config.StatusReMapper

class StatusReMapperTest extends org.scalatest.FunSuite {

  def now = new Timestamp(System.currentTimeMillis()).getTime

  test("Strings beginning with emerg or f (case-insensitive) map to emerg (0)") {

    val config = StatusReMapper(List("field1", "field2", "field3"))
    val attributes =
      Json.obj("field1" -> "Emergencey message".asJson, "field2" -> "Info message".asJson, "field3" -> "warning".asJson)
    val record = LogRecord("Log Message", Some(Info), Some(now), attributes = attributes)
    val runner = new StatusReMapperRunner
    val result = runner.run(config, record)

    assert(result.level.contains(Emergency))
  }

  test("Should match syslog level numbers to correct Level name") {
    val config = StatusReMapper(List("field1", "field2", "field3"))
    val attributes =
      Json.obj("field1" -> "value1".asJson, "field2" -> "5".asJson, "field3" -> "notice".asJson)
    val record = LogRecord("Log Message", Some(Info), Some(now), attributes = attributes)
    val runner = new StatusReMapperRunner
    val result = runner.run(config, record)

    assert(result.level.contains(Warning))
  }

  test("Should not be case sensitive") {
    val config = StatusReMapper(List("field1", "field2", "field3"))
    val attributes =
      Json.obj("field1" -> "Notice".asJson, "field2" -> "5".asJson, "field3" -> "ErRor".asJson)
    val record = LogRecord("Log Message", Some(Info), Some(now), attributes = attributes)
    val runner = new StatusReMapperRunner
    val result = runner.run(config, record)

    assert(result.level.contains(Notice))
  }

}
