package logpack.processor.runners

import java.util.regex.Pattern

import io.circe.syntax._
import logpack.LogRecord
import logpack.config.GrokParser
import logpack.processor.ProcessorRunner
import logpack.processor.ProcessorHelper.merge

class GrokParserRunner extends ProcessorRunner[GrokParser] {

  override def run(processor: GrokParser, record: LogRecord): LogRecord = {
    val attrs = processor.matchRules
      .to(LazyList)
      .flatMap(parse(_, record.message))
      .headOption
      .getOrElse(Map.empty)
      .asJson

    record.copy(attributes = merge(record.attributes, attrs))
  }

  def parse(pattern: String, str: String): Option[Map[String, String]] = {
    val matcher = Pattern.compile(pattern).matcher(str)

    if (matcher.matches()) {
      val attributes = getAttributes(pattern)
      val values     = attributes.map(matcher.group)
      Some(attributes.zip(values).toMap)
    } else None

  }

  def getAttributes(rule: String): Seq[String] =
    raw"""\?<(\w+)>""".r.findAllMatchIn(rule).toList.map(_.group(1))
}
