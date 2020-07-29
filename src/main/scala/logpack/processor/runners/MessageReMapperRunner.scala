package logpack.processor.runners

import logpack.LogRecord
import logpack.config.MessageReMapper
import logpack.processor.ProcessorHelper.{deleteField, tryFind}
import logpack.processor.ProcessorRunner

class MessageReMapperRunner extends ProcessorRunner[MessageReMapper] {

  override def run(processor: MessageReMapper, record: LogRecord): LogRecord = {
    val message = processor.sources
      .to(LazyList)
      .flatMap(f => tryFind(f, record.attributes).map(j => (f, j)))
      .headOption

    message match {
      case Some((_, v)) if processor.preserveSource && v.isString =>
        record.copy(message = v.asString.get)

      case Some((f, v)) if !processor.preserveSource && v.isString =>
        val attrs = deleteField(record.attributes, f)
        record.copy(message = v.asString.get, attributes = attrs)

      case _ => record
    }
  }

}
