package logpack.processor.runners

import logpack.LogRecord
import logpack.config.GeoIPParser
import logpack.processor.ProcessorRunner

class GeoIPParserRunner extends ProcessorRunner[GeoIPParser] {
  override def run(processor: GeoIPParser, record: LogRecord): LogRecord =
    record
}
