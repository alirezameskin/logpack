package logpack.processor.runners

import logpack.LogRecord
import logpack.config.MessageReMapper
import logpack.processor.ProcessorHelper.tryFind
import logpack.processor.ProcessorRunner

class MessageReMapperRunner extends ProcessorRunner[MessageReMapper] {

  override def run(processor: MessageReMapper, record: LogRecord): LogRecord = {
    val message = processor.sources
      .to(LazyList)
      .flatMap(f => tryFind(f, record.attributes))
      .headOption

    message match {
      case Some(v) if v.isString => record.copy(message = v.asString.get)
      case _                     => record
    }
  }

}
