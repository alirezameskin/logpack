package logpack.processor.runners

import io.circe.Json
import io.circe.syntax._
import logpack._
import logpack.config.ReMapper

class ReMapperTest extends org.scalatest.FunSuite {

  test("Remap an existing field without removing it") {

    val config     = ReMapper(List("newName"), "oldName", true)
    val attributes = Json.obj("newName" -> "data1".asJson)

    val record = LogRecord("Log Message", Some(Info), None, attributes = attributes)
    val runner = new ReMapperRunner
    val result = runner.run(config, record)

    val expected = Json.obj(
      "oldName" -> "data1".asJson,
      "newName" -> "data1".asJson
    )

    assert(result.attributes == expected)
  }

  test("Remap an existing field and removing the old one") {

    val config     = ReMapper(List("existing_field"), "new_defined_field", false)
    val attributes = Json.obj("existing_field" -> "data1".asJson)

    val record = LogRecord("Log Message", Some(Info), None, attributes = attributes)
    val runner = new ReMapperRunner
    val result = runner.run(config, record)

    val expected = Json.obj(
      "new_defined_field" -> "data1".asJson
    )

    assert(result.attributes == expected)
  }
}
