package logpack.processor.runners

import io.circe.Json
import logpack.LogRecord
import logpack.config.ReMapper
import logpack.processor.ProcessorHelper.{createJson, merge, tryFind}
import logpack.processor.ProcessorRunner

class ReMapperRunner extends ProcessorRunner[ReMapper] {

  override def run(processor: ReMapper, record: LogRecord): LogRecord = {
    val result = processor.sources
      .to(LazyList)
      .flatMap(f => tryFind(f, record.attributes).map(j => (f, j)))
      .headOption

    result match {
      case Some((_, value)) if processor.preserveSource =>
        val attrs = merge(record.attributes, createJson(processor.target, value))
        record.copy(attributes = attrs)

      case Some((field, value)) if !processor.preserveSource =>
        val attrsWithSource = merge(record.attributes, createJson(processor.target, value))
        val attrs           = merge(attrsWithSource, createJson(field, Json.Null)).dropNullValues
        record.copy(attributes = attrs)

      case None => record
    }
  }

}
