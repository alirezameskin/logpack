package logpack.processor.runners

import logpack.LogRecord
import logpack.config.ReMapper
import logpack.processor.{ProcessorHelper, ProcessorRunner}

class ReMapperRunner extends ProcessorRunner[ReMapper] {
  override def run(processor: ReMapper, record: LogRecord): LogRecord =
    firstAttribute(processor.sources, record) match {
      case Some(value) =>
        val attrs = merge(record.attributes, ProcessorHelper.createMap(processor.target, value))
        record.copy(attributes = attrs)

      case None => record
    }
}
