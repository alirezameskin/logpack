package logpack.processor.runners

import logpack.LogRecord
import logpack.config.ReMapper
import logpack.processor.ProcessorHelper.{createJson, merge, tryFind}
import logpack.processor.ProcessorRunner

class ReMapperRunner extends ProcessorRunner[ReMapper] {

  override def run(processor: ReMapper, record: LogRecord): LogRecord = {
    val field = processor.sources
      .to(LazyList)
      .flatMap(f => tryFind(f, record.attributes))
      .headOption

    field match {
      case Some(value) =>
        val attrs = merge(record.attributes, createJson(processor.target, value))
        record.copy(attributes = attrs)

      case None => record
    }
  }

}
