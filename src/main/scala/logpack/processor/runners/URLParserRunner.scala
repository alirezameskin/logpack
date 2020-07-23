package logpack.processor.runners

import java.net.URI

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import logpack.config.URLParser
import logpack.processor.ProcessorHelper.merge
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import logpack.{LogRecord, UrlDetails}

import scala.util.Try

class URLParserRunner extends ProcessorRunner[URLParser] {

  override def run(processor: URLParser, record: LogRecord): LogRecord = {
    val details: Option[Json] =
      processor.sources
        .to(LazyList)
        .flatMap(f => checkSource(record, f))
        .headOption
        .map(details => details.asJson)

    val attrs = details match {
      case Some(obj) => ProcessorHelper.createJson(processor.target, obj)
      case None      => Json.Null
    }

    record.copy(attributes = merge(record.attributes, attrs))
  }

  private def checkSource(record: LogRecord, field: String): Option[UrlDetails] =
    ProcessorHelper
      .tryFind(field, record.attributes)
      .filter(_.isString)
      .flatMap(_.asString)
      .flatMap(parseUrl)

  private def parseUrl(url: String): Option[UrlDetails] =
    Try {
      val uri = new URI(url)
      uri.getUserInfo
      UrlDetails(
        scheme = Option(uri.getScheme),
        host = Option(uri.getHost),
        port = Option(uri.getPort).filter(_ != -1),
        path = Option(uri.getPath),
        fragment = Option(uri.getFragment)
      )
    }.toOption
}
