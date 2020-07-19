package logpack

import java.nio.file.Path

import cats.effect.{ExitCode, IO}
import cats.implicits._
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp

case class ExecuteOptions(pretty: Boolean = false, config: Path)

object LogPackApp extends CommandIOApp(name = "logpack", header = "Log Processor tool", version = "0.1") {

  val prettyOpts: Opts[Boolean] =
    Opts.flag("pretty", "Pretty print.", short = "p").orFalse

  val configFileOpts: Opts[Path] =
    Opts.argument[Path](metavar = "config")

  val executeOptions: Opts[ExecuteOptions] =
    (prettyOpts, configFileOpts).mapN(ExecuteOptions)

  override def main: Opts[IO[ExitCode]] = executeOptions.map { options =>
    ExecuteCommand.execute(options)
  }
}
