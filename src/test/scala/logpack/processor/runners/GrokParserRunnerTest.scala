package logpack.processor.runners

import io.circe.Json
import io.circe.syntax._
import logpack._
import logpack.config.{Arithmetic, GrokParser}

class GrokParserRunnerTest extends org.scalatest.FunSuite {

  test("Test Grok") {

    val config = GrokParser(
      List(
        """^(?<time>\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2}) \[(?<level>\w+)\] (?<pid>\d+).(?<tid>\d+): (?<message>.*)"""
      )
    )

    val log = "2020/07/12 12:25:34 [Info] 3456.24: Test Message"

    val record = LogRecord(log, Some(Info), None, attributes = Json.obj())
    val runner = new GrokParserRunner
    val result = runner.run(config, record)

    val expected = Json.obj(
      "pid"     -> "3456".asJson,
      "tid"     -> "24".asJson,
      "message" -> "Test Message".asJson,
      "time"    -> "2020/07/12 12:25:34".asJson,
      "level"   -> "Info".asJson
    )

    assert(result.attributes == expected)
  }

}
