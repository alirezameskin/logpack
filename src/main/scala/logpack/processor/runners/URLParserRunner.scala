package logpack.processor.runners

import java.net.URI

import logpack.config.URLParser
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import logpack.{LogRecord, UrlDetails}

import scala.util.Try

class URLParserRunner extends ProcessorRunner[URLParser] {

  override def run(processor: URLParser, record: LogRecord): LogRecord = {
    val details =
      processor.sources
        .to(LazyList)
        .map(f => checkSource(record, f))
        .filter(_.isDefined)
        .take(1)
        .headOption
        .flatten

    val attrs = ProcessorHelper.createMap(processor.target, details)

    record.copy(attributes = record.attributes ++ attrs)
  }

  private def checkSource(record: LogRecord, field: String): Option[UrlDetails] =
    ProcessorHelper
      .tryFind(field, record.attributes)
      .map(_.toString)
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
