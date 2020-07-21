package logpack.processor

import io.circe.Json
import logpack.LogRecord
import logpack.config._
import logpack.processor.runners.implicits._

trait ProcessorRunner[A <: Processor] {

  def merge(attrs1: Json, attrs2: Json): Json = (attrs1, attrs2) match {
    case (Json.Null, Json.Null) => Json.Null
    case (Json.Null, _)         => attrs2
    case (_, Json.Null)         => attrs1
    case (_, _)                 => attrs1.deepMerge(attrs2)
  }

  def run(processor: A, record: LogRecord): LogRecord

  def firstAttribute(names: List[String], record: LogRecord): Option[Json] =
    names
      .to(LazyList)
      .map(f => ProcessorHelper.tryFind(f, record.attributes))
      .filter(_.isDefined)
      .map(_.get)
      .headOption
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
