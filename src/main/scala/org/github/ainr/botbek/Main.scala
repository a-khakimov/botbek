package org.github.ainr.botbek

import cats.effect.kernel.GenTemporal
import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.botbek.conf.Config
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.schedule.ScheduledTasks
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.module.UnsplashModule
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.FiniteDuration

object Main extends IOApp {

  implicit val timer: Timer[IO] =
    (duration: FiniteDuration) => GenTemporal[IO].sleep(duration)

  val app: IO[Unit] = BlazeClientBuilder[IO]
    .resource
    .use { httpClient =>
      for {
        logger <- Slf4jLogger.create[IO]
        config <- Config.make[IO]()
        unsplash = UnsplashModule[IO](config.unsplash, httpClient)
        bot = BotBek.make[IO](config.telegram, httpClient)
        scheduledTasks = {
          implicit val logger0: SelfAwareStructuredLogger[IO] = logger
          ScheduledTasks[IO](unsplash.unsplashService, bot)
        }
        botFiber <- bot.start.start
        scheduledTasksFiber <- scheduledTasks.run.start
        _ <- botFiber.join
        _ <- scheduledTasksFiber.join
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    app.as(ExitCode.Success)
}
