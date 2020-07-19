package logpack.processor.runners

import logpack.LogRecord
import logpack.config.ReMapper
import logpack.processor.ProcessorRunner

class ReMapperRunner extends ProcessorRunner[ReMapper] {
  override def run(processor: ReMapper, record: LogRecord): LogRecord =
    firstAttribute(processor.sources, record) match {
      case Some(value) =>
        val attributes = addAttribute(processor.target, value, record.attributes)
        record.copy(attributes = attributes)

      case None => record
    }
}
