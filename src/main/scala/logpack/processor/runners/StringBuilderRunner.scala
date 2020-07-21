package logpack.processor.runners

import io.circe.syntax._
import logpack.LogRecord
import logpack.config.StringBuilderProcessor
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import sparser.template

class StringBuilderRunner extends ProcessorRunner[StringBuilderProcessor] {

  override def run(processor: StringBuilderProcessor, record: LogRecord): LogRecord =
    template.evaluate(
      processor.template,
      x => ProcessorHelper.tryFind(x, record.attributes).map(_.toString)
    ) match {
      case Right(value) =>
        val attrs = merge(record.attributes, ProcessorHelper.createMap(processor.target, value.asJson))
        record.copy(attributes = attrs)

      case Left(_) => record
    }
}
