package logpack.processor.runners

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
        record.copy(
          attributes = record.attributes ++ ProcessorHelper
            .createMap(processor.target, value)
        )
      case Left(_) => record
    }
}
