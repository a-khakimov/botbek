package org.github.ainr.botbek.infrastructure

import scala.concurrent.duration.FiniteDuration

trait Timer[F[_]] {
  def sleep(duration: FiniteDuration): F[Unit]
}

object Timer {
  def apply[F[_]](
      implicit
      timer: Timer[F]
  ): Timer[F] = timer
}

/*
object Test extends IOApp {

  import syntax.schedulerSyntax

  implicit val timer: Timer[IO] =
    (duration: FiniteDuration) => GenTemporal[IO].sleep(duration)

  def foo[F[_]: Applicative: Timer: MonadThrow](): F[Unit] =
    42.pure[F].map(println)

  override def run(args: List[String]): IO[ExitCode] = {

    foo[IO]().every(5.seconds).as(ExitCode.Success)
  }
}
 */
