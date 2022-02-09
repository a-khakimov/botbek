package org.github.ainr.botbek

import cats.effect.kernel.GenTemporal
import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.botbek.conf.Config
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.module.UnsplashModule
import org.http4s.blaze.client.BlazeClientBuilder

import scala.concurrent.duration.FiniteDuration

object Main extends IOApp {

  implicit val timer: Timer[IO] =
    (duration: FiniteDuration) => GenTemporal[IO].sleep(duration)

  val app: IO[Unit] = BlazeClientBuilder[IO]
    .resource
    .use { httpClient =>
      for {
        config <- Config.make[IO]()
        unsplash = UnsplashModule[IO](config.unsplash, httpClient)
        bot = BotBek.make[IO](
          config.telegram,
          httpClient
        )(
          unsplash.unsplashService
        )
        _ <- bot.runScheduledTasks().start
        _ <- bot.start()
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    app.as(ExitCode.Success)
}
