package logpack.processor.runners

import logpack.config.UserAgentParser
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import logpack.{LogRecord, UserAgent}
import nl.basjes.parse.useragent.UserAgentAnalyzer

import scala.util.Try

class UserAgentParserRunner extends ProcessorRunner[UserAgentParser] {

  val analyzer = UserAgentAnalyzer
    .newBuilder()
    .hideMatcherLoadStats()
    .withCache(10000)
    .build();

  override def run(processor: UserAgentParser, record: LogRecord): LogRecord = {

    val details =
      processor.sources
        .to(LazyList)
        .map(f => check(record, f))
        .filter(_.isDefined)
        .take(1)
        .headOption
        .flatten

    val attrs = details
      .map { agent =>
        ProcessorHelper.createMap(processor.target, agent)
      }
      .getOrElse(record.attributes)

    record.copy(attributes = record.attributes ++ attrs)
  }

  private def check(record: LogRecord, field: String): Option[UserAgent] =
    ProcessorHelper
      .tryFind(field, record.attributes)
      .map(_.toString)
      .flatMap(parse)

  private def parse(content: String): Option[UserAgent] =
    Try {
      val result = analyzer.parse(content)
      UserAgent(
        result.getValue("DeviceClass"),
        result.getValue("OPERATING_SYSTEM_CLASS"),
        result.getValue("AgentClass"),
        result.getValue("AGENT_VERSION")
      )
    }.toOption
}
