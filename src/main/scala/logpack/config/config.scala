package logpack.config

final case class Config(steps: List[StepConfig])
final case class StepConfig(processor: Processor)
final case class CategoryItem(name: String, query: String)

sealed trait Processor

final case class Arithmetic(expression: String, target: String)                 extends Processor
final case class CategoryParser(categories: List[CategoryItem], target: String) extends Processor
final case class GeoIPParser(sources: List[String], target: String)             extends Processor
final case class GrokParser(matchRules: Seq[String])                            extends Processor
final case class StringBuilderProcessor(template: String, target: String)       extends Processor
final case class URLParser(sources: List[String], target: String)               extends Processor
final case class UserAgentParser(sources: List[String], target: String)         extends Processor

final case class LookupProcessor(source: String, target: String, lookupTable: Map[String, String], default: String)
    extends Processor

final case class DateReMapper(sources: List[String], format: Option[String] = None, preserveSource: Boolean = false)
    extends Processor
final case class ReMapper(sources: List[String], target: String, preserveSource: Boolean = false) extends Processor
final case class MessageReMapper(sources: List[String], preserveSource: Boolean = false)          extends Processor
final case class StatusReMapper(sources: List[String])                                            extends Processor
