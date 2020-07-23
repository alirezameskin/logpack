package logpack.processor.runners

import io.circe.Json
import io.circe.syntax._
import logpack._
import logpack.config.Arithmetic

class ArithmeticRunnerTest extends org.scalatest.funsuite.AnyFunSuite {

  test("Run arithmetic") {

    val config     = Arithmetic("${processing_secs} * 1000 + ${waiting_time}", "latency")
    val attributes = Json.obj("processing_secs" -> 1.asJson, "waiting_time" -> 125.asJson, "type" -> "Web".asJson)

    val record = LogRecord("Log Message", Some(Info), None, attributes = attributes)
    val runner = new ArithmeticRunner
    val result = runner.run(config, record)

    val expected = Json.obj(
      "processing_secs" -> 1.asJson,
      "waiting_time"    -> 125.asJson,
      "type"            -> "Web".asJson,
      "latency"         -> 1125.asJson
    )

    assert(result.attributes == expected)
  }

}
