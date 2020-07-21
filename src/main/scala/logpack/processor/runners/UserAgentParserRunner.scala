package logpack.processor.runners

import eu.bitwalker.useragentutils
import logpack.config.UserAgentParser
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import logpack.{LogRecord, UserAgent}

import scala.util.Try

class UserAgentParserRunner extends ProcessorRunner[UserAgentParser] {

  override def run(processor: UserAgentParser, record: LogRecord): LogRecord = {

    val details =
      processor.sources
        .to(LazyList)
        .map(f => check(record, f))
        .filter(_.isDefined)
        .map(_.get)
        .headOption

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
      val agent = useragentutils.UserAgent.parseUserAgentString(content)
      UserAgent(
        Option(agent.getOperatingSystem).map(_.getDeviceType).map(_.getName),
        Option(agent.getOperatingSystem).map(_.getGroup).map(_.getName),
        Option(agent.getBrowser).map(_.getGroup).map(_.getName),
        Option(agent.getBrowserVersion).map(_.getVersion)
      )
    }.toOption
}
