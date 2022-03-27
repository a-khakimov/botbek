package org.github.ainr.botbek

import cats.syntax.all.*
import cats.effect.kernel.GenTemporal
import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.botbek.conf.Config
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.infrastructure.logger.ContextLogger
import org.github.ainr.botbek.schedule.ScheduledTasks
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.tg.module.TelegramModule
import org.github.ainr.botbek.unsplash.module.UnsplashModule
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.FiniteDuration

object BotbekApp extends IOApp {

  given timer: Timer[IO] =
    (duration: FiniteDuration) => GenTemporal[IO].sleep(duration)

  val app: IO[Unit] = BlazeClientBuilder[IO]
    .resource
    .use { httpClient =>
      for {
        slf4jLogger <-
          Slf4jLogger.fromName[IO]("org.github.ainr.botbek.BotbekApp")
        contextLogger = ContextLogger[IO](slf4jLogger)
        config <- Config.load[IO]
        unsplash = UnsplashModule[IO](
          config.unsplash,
          httpClient
        )
        telegram = TelegramModule[IO](
          config.telegram,
          httpClient,
          contextLogger
        )
        scheduledTasks = {
          given logger0: SelfAwareStructuredLogger[IO] = slf4jLogger
          ScheduledTasks[IO](
            unsplash.unsplashService,
            telegram.botBek
          )
        }
        botFiber <- telegram.botBek.start().start
        _ <- botFiber.join
        _ <- scheduledTasks.run
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    app.as(ExitCode.Success)
}
