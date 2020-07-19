package logpack.processor.runners

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

    record.copy(
      attributes = record.attributes ++ ProcessorHelper
        .createMap(processor.target, result)
    )
  }
}
