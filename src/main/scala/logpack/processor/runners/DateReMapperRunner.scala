package logpack.processor.runners

import java.sql.Timestamp
import java.time.LocalDateTime

import logpack.LogRecord
import logpack.config.DateReMapper
import logpack.processor.{ProcessorHelper, ProcessorRunner}

class DateReMapperRunner extends ProcessorRunner[DateReMapper] {

  override def run(processor: DateReMapper, record: LogRecord): LogRecord = {

    val toDate: String => Option[LocalDateTime] = x =>
      processor.format match {
        case Some(format) => ProcessorHelper.toDate(x, format)
        case None         => ProcessorHelper.toDate(x)
      }

    firstAttribute(processor.sources, record).flatMap(toDate) match {
      case Some(v) => record.copy(time = Some(Timestamp.valueOf(v).getTime))
      case _       => record
    }
  }

}