package logpack.processor.runners

object implicits {
  implicit val arithmetic             = new ArithmeticRunner
  implicit val categoryParser         = new CategoryParserRunner
  implicit val dateReMapper           = new DateReMapperRunner
  implicit val geoIPParser            = new GeoIPParserRunner
  implicit val grokParser             = new GrokParserRunner
  implicit val lookupProcessor        = new LookupProcessorRunner
  implicit val messageReMapper        = new MessageReMapperRunner
  implicit val reMapper               = new ReMapperRunner
  implicit val statusReMapper         = new StatusReMapperRunner
  implicit val stringBuilderProcessor = new StringBuilderRunner
  implicit val uRLParser              = new URLParserRunner
  implicit val userAgentParser        = new UserAgentParserRunner
}
