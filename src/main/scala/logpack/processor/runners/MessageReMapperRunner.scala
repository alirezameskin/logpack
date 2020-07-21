package logpack.processor.runners

import logpack.LogRecord
import logpack.config.MessageReMapper
import logpack.processor.ProcessorRunner

class MessageReMapperRunner extends ProcessorRunner[MessageReMapper] {

  override def run(processor: MessageReMapper, record: LogRecord): LogRecord =
    firstAttribute(processor.sources, record) match {
      case Some(v) if v.isString => record.copy(message = v.asString.get)
      case _                     => record
    }

}
