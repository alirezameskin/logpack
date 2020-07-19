package logpack

import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.{Decoder, DecodingFailure, HCursor}

package object config {
  implicit val configDecoder: Decoder[Config] = deriveDecoder

  implicit val stepConfigDecoder: Decoder[StepConfig] =
    new Decoder[StepConfig] {
      override def apply(c: HCursor): Result[StepConfig] = {
        c.downField("type")
          .as[String]
          .flatMap {
            case "grok-parser"              => c.as[GrokParser]
            case "date-remapper"            => c.as[DateReMapper]
            case "status-remapper"          => c.as[StatusReMapper]
            case "message-remapper"         => c.as[MessageReMapper]
            case "remapper"                 => c.as[ReMapper]
            case "url-parser"               => c.as[URLParser]
            case "user-agent-parser"        => c.as[UserAgentParser]
            case "category-processor"       => c.as[CategoryParser]
            case "arithmetic-processor"     => c.as[Arithmetic]
            case "string-builder-processor" => c.as[StringBuilderProcessor]
            case "geo-ip-parser"            => c.as[GeoIPParser]
            case "lookup-processor"         => c.as[LookupProcessor]
            case t                          => Left(DecodingFailure(s"Invalid type $t", c.history))
          }
          .map(StepConfig(_))
      }
    }
}
