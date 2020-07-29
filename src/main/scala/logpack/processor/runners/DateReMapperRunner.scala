package logpack.processor.runners

import java.sql.Timestamp
import java.time.LocalDateTime

import logpack.LogRecord
import logpack.config.DateReMapper
import logpack.processor.ProcessorHelper.{deleteField, tryFind}
import logpack.processor.{ProcessorHelper, ProcessorRunner}

class DateReMapperRunner extends ProcessorRunner[DateReMapper] {

  override def run(processor: DateReMapper, record: LogRecord): LogRecord = {

    val toDate: String => Option[LocalDateTime] = x => {
      processor.format match {
        case Some(format) => ProcessorHelper.toDate(x, format)
        case None         => ProcessorHelper.toDate(x)
      }
    }

    val date = processor.sources
      .to(LazyList)
      .flatMap { f =>
        tryFind(f, record.attributes)
          .filter(_.isString)
          .flatMap(_.asString)
          .flatMap(toDate)
          .map(j => (f, j))
      }
      .headOption

    date match {
      case Some((_, v)) if processor.preserveSource =>
        record.copy(time = Some(Timestamp.valueOf(v).getTime))

      case Some((field, value)) if !processor.preserveSource =>
        val attrs = deleteField(record.attributes, field)
        record.copy(time = Some(Timestamp.valueOf(value).getTime), attributes = attrs)

      case _ => record
    }
  }

}
