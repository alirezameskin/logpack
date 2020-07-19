package logpack.processor.runners

import logpack.LogRecord
import logpack.config.CategoryParser
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import sparser.conditional

class CategoryParserRunner extends ProcessorRunner[CategoryParser] {

  override def run(processor: CategoryParser, record: LogRecord): LogRecord = {
    val resolver = x => ProcessorHelper.tryFind(x, record.attributes)
    val result = processor.categories
      .to(LazyList)
      .filter(c => isMatched(c.query, resolver))
      .map(_.name)
      .collectFirst { case s => s }

    result match {
      case Some(value) =>
        record.copy(
          attributes = record.attributes ++ ProcessorHelper
            .createMap(processor.target, value)
        )
      case None => record
    }
  }

  private def isMatched(expr: String, vars: String => Option[Any]): Boolean =
    conditional.evaluate(expr, vars) match {
      case Left(_)  => false
      case Right(_) => true
    }
}
