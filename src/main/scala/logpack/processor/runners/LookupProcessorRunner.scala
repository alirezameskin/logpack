package logpack.processor.runners

import io.circe.syntax._
import logpack.LogRecord
import logpack.config.LookupProcessor
import logpack.processor.{ProcessorHelper, ProcessorRunner}

class LookupProcessorRunner extends ProcessorRunner[LookupProcessor] {

  override def run(processor: LookupProcessor, record: LogRecord): LogRecord = {
    val result = (for {
      source <- ProcessorHelper
        .tryFind(processor.source, record.attributes)
        .map(_.toString)
      targetValue <- processor.lookupTable.get(source)
    } yield targetValue).getOrElse(processor.default)

    val attrs = merge(record.attributes, ProcessorHelper.createMap(processor.target, result.asJson))
    record.copy(attributes = attrs)
  }
}
