package org.github.ainr.botbek

import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.botbek.conf.Config
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.module.UnsplashModule
import org.http4s.blaze.client.BlazeClientBuilder

object Main extends IOApp {

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
        _ <- bot.start()
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    app.as(ExitCode.Success)
}
