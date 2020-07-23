package logpack.processor.runners

import java.sql.Timestamp

import io.circe.Json
import io.circe.syntax._
import logpack.config.URLParser
import logpack.{Info, LogRecord}

class URLParserTest extends org.scalatest.funsuite.AnyFunSuite {
  def now = new Timestamp(System.currentTimeMillis()).getTime

  test("Should parse a valid URL and convert it to UrlDetails") {
    val config = URLParser(List("field1", "field2", "field3"), "http.url_details")

    val attributes =
      Json.obj(
        "field1" -> "Emergencey message".asJson,
        "field2" -> "https://github.com/typelevel/cats-effect".asJson,
        "field3" -> "https://app.pluralsight.com/course-player".asJson
      )

    val runner = new URLParserRunner
    val record = LogRecord("Log Message", Some(Info), Some(now), attributes)
    val result = runner.run(config, record)

    assert((result.attributes \\ "http").size == 1)
    assert((result.attributes \\ "http").head.isObject)
    assert((result.attributes \\ "http").head.asObject.get.toMap.contains("url_details"))

    val expected = Json
      .obj(
        "http" -> Json.obj(
          "url_details" -> Json.obj(
            "scheme"   -> "https".asJson,
            "host"     -> "github.com".asJson,
            "port"     -> Json.Null,
            "path"     -> "/typelevel/cats-effect".asJson,
            "query"    -> Json.Null,
            "fragment" -> Json.Null
          )
        )
      )
      .deepMerge(attributes)

    assert(result.attributes == expected)
  }
}
