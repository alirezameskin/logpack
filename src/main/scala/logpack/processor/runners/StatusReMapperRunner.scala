package logpack.processor.runners

import logpack._
import logpack.config.StatusReMapper
import logpack.processor.{ProcessorHelper, ProcessorRunner}

class StatusReMapperRunner extends ProcessorRunner[StatusReMapper] {

  val SyslogLevels = List("0", "1", "2", "3", "4", "5", "6", "7")

  override def run(processor: StatusReMapper, record: LogRecord): LogRecord = {
    val level = processor.sources
      .to(LazyList)
      .flatMap(f => checkField(record, f))
      .headOption

    record.copy(level = level)
  }

  private def checkField(record: LogRecord, field: String): Option[Level] =
    ProcessorHelper
      .tryFind(field, record.attributes)
      .flatMap(j => j.asNumber.map(_.toString).orElse(j.asString))
      .map(_.toLowerCase)
      .flatMap {
        case x if SyslogLevels.contains(x)                   => mapToLevel(x.toInt)
        case x if x.startsWith("emerg") || x.startsWith("f") => Some(Emergency)
        case x if x.startsWith("a")                          => Some(Alert)
        case x if x.startsWith("c")                          => Some(Critical)
        case x if x.startsWith("err")                        => Some(logpack.Error)
        case x if x.startsWith("w")                          => Some(Warning)
        case x if x.startsWith("n")                          => Some(Notice)
        case x if x.startsWith("i")                          => Some(logpack.Info)
        case x if x.startsWith("d")                          => Some(logpack.Debug)
        case x if x.startsWith("trace")                      => Some(logpack.Debug)
        case x if x.startsWith("verbose")                    => Some(logpack.Debug)
        case x if x.startsWith("o")                          => Some(logpack.Info)
        case x if x == "success"                             => Some(logpack.Info)
        case x if x == "ok"                                  => Some(logpack.Info)
        case _                                               => None
      }

  private def mapToLevel(l: Int): Option[Level] = l match {
    case 0 => Some(Emergency)
    case 2 => Some(Alert)
    case 3 => Some(Critical)
    case 4 => Some(logpack.Error)
    case 5 => Some(Warning)
    case 6 => Some(Notice)
    case 7 => Some(logpack.Info)
    case 8 => Some(logpack.Debug)
    case _ => None
  }
}
