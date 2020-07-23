package logpack.processor.runners

import io.circe.syntax._
import logpack.LogRecord
import logpack.config.Arithmetic
import logpack.processor.ProcessorHelper.merge
import logpack.processor.{ProcessorHelper, ProcessorRunner}
import sparser.arithmetic

class ArithmeticRunner extends ProcessorRunner[Arithmetic] {

  override def run(processor: Arithmetic, record: LogRecord): LogRecord =
    arithmetic.evaluate(
      processor.expression,
      x => ProcessorHelper.tryFind(x, record.attributes).map(_.toString.toDouble)
    ) match {
      case Left(_) => record
      case Right(value) =>
        val attrs = merge(ProcessorHelper.createJson(processor.target, value.asJson), record.attributes)
        record.copy(attributes = attrs)
    }

}
