package logpack.processor.runners

import io.circe.syntax._
import logpack.LogRecord
import logpack.config.CategoryParser
import logpack.processor.ProcessorHelper.{createJson, merge}
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import sparser.conditional

import scala.util.Try

class CategoryParserRunner extends ProcessorRunner[CategoryParser] {

  override def run(processor: CategoryParser, record: LogRecord): LogRecord = {
    val resolver: String => Option[Double] = x =>
      ProcessorHelper.tryFind(x, record.attributes).flatMap {
        case n if n.isNumber => Some(n.asNumber.get.toDouble)
        case n if n.isString => Try(n.asString.map(_.toDouble)).toOption.flatten
        case _               => None
      }

    val result = processor.categories
      .to(LazyList)
      .filter(c => isMatched(c.query, resolver))
      .map(_.name)
      .collectFirst { case s => s }

    result match {
      case Some(value) =>
        val attrs = merge(createJson(processor.target, value.asJson), record.attributes)
        record.copy(attributes = attrs)

      case None => record
    }
  }

  private def isMatched(expr: String, vars: String => Option[Any]): Boolean =
    conditional.evaluate(expr, vars) match {
      case Left(_)  => false
      case Right(_) => true
    }
}
