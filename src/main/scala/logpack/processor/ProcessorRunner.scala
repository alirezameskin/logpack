package logpack.processor

import logpack.LogRecord
import logpack.config._
import logpack.processor.runners.implicits._

trait ProcessorRunner[A <: Processor] {
  def run(processor: A, record: LogRecord): LogRecord

  def firstAttribute(names: List[String], record: LogRecord): Option[String] =
    names
      .to(LazyList)
      .map(f => ProcessorHelper.tryFind(f, record.attributes).map(_.toString))
      .filter(_.isDefined)
      .map(_.get)
      .headOption

  def addAttribute(field: String, value: Any, attributes: Map[String, Any]): Map[String, Any] =
    attributes ++ ProcessorHelper.createMap(field, value)
}

object ProcessorRunner {

  def pipeline(steps: Seq[Processor], record: LogRecord): LogRecord =
    steps.foldLeft(record) { (rec, step) =>
      run(step, rec)
    }

  private def run(processor: Processor, record: LogRecord): LogRecord =
    processor match {
      case prs: Arithmetic             => implicitly[ProcessorRunner[Arithmetic]].run(prs, record)
      case prs: CategoryParser         => implicitly[ProcessorRunner[CategoryParser]].run(prs, record)
      case prs: DateReMapper           => implicitly[ProcessorRunner[DateReMapper]].run(prs, record)
      case prs: GeoIPParser            => implicitly[ProcessorRunner[GeoIPParser]].run(prs, record)
      case prs: GrokParser             => implicitly[ProcessorRunner[GrokParser]].run(prs, record)
      case prs: LookupProcessor        => implicitly[ProcessorRunner[LookupProcessor]].run(prs, record)
      case prs: MessageReMapper        => implicitly[ProcessorRunner[MessageReMapper]].run(prs, record)
      case prs: ReMapper               => implicitly[ProcessorRunner[ReMapper]].run(prs, record)
      case prs: StatusReMapper         => implicitly[ProcessorRunner[StatusReMapper]].run(prs, record)
      case prs: StringBuilderProcessor => implicitly[ProcessorRunner[StringBuilderProcessor]].run(prs, record)
      case prs: URLParser              => implicitly[ProcessorRunner[URLParser]].run(prs, record)
      case prs: UserAgentParser        => implicitly[ProcessorRunner[UserAgentParser]].run(prs, record)
    }
}
