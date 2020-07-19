package logpack

import java.io.FileReader

import cats.effect.{Blocker, ContextShift, ExitCode, IO}
import cats.implicits._
import fs2.{text, Pipe}
import logpack.config.{Config, Processor}
import logpack.processor.ProcessorRunner

object ExecuteCommand {

  def process(steps: Seq[Processor]): Pipe[IO, String, LogRecord] = lines => {
    lines
      .map(LogRecord(_))
      .map(rec => ProcessorRunner.pipeline(steps, rec))
  }

  def program(steps: List[Processor], pretty: Boolean)(implicit cs: ContextShift[IO]): fs2.Stream[IO, Unit] =
    fs2.Stream.resource(Blocker[IO]).flatMap { blocker =>
      implicit val show = if (pretty) LogRecord.PRETTY_PRINT else LogRecord.ONE_LINE_PRINT

      val stdin  = fs2.io.stdin[IO](10, blocker)
      val stdout = fs2.io.stdoutLines[IO, LogRecord](blocker)

      stdin
        .through(text.utf8Decode)
        .through(text.lines)
        .through(process(steps))
        .through(stdout)
    }

  def execute(options: ExecuteOptions)(implicit cs: ContextShift[IO]): IO[ExitCode] =
    for {
      file   <- IO(new FileReader(options.config.toFile))
      json   <- io.circe.yaml.parser.parse(file).liftTo[IO]
      config <- IO.fromEither(json.as[Config])
      steps = config.steps.map(_.processor)
      res <- program(steps, options.pretty).compile.drain.as(ExitCode.Success)
    } yield res
}
