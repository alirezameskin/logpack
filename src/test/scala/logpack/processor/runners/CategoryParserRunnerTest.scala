package logpack.processor.runners

import io.circe.Json
import io.circe.syntax._
import logpack._
import logpack.config.{CategoryItem, CategoryParser}

class CategoryParserRunnerTest extends org.scalatest.funsuite.AnyFunSuite {

  test("Run Category Parser runner") {

    val config = CategoryParser(
      List(
        CategoryItem("OK", "status:[200 TO 299]"),
        CategoryItem("Notice", "status:[300 TO 399]"),
        CategoryItem("Warning", "status:[400 TO 499]"),
        CategoryItem("Error", "status:[500 TO 599]")
      ),
      "http.category"
    );

    val attributes = Json.obj("status" -> 204.asJson, "waiting_time" -> 125.asJson, "type" -> "Web".asJson)

    val record = LogRecord("Log Message", Some(Info), None, attributes = attributes)
    val runner = new CategoryParserRunner
    val result = runner.run(config, record)

    val expected = Json.obj(
      "status"       -> 204.asJson,
      "waiting_time" -> 125.asJson,
      "type"         -> "Web".asJson,
      "http" -> Json.obj(
        "category" -> "OK".asJson
      )
    )

    assert(result.attributes == expected)
  }

}
