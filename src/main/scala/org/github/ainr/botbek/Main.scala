package org.github.ainr.botbek

import cats.effect.{ExitCode, IO, IOApp}
import org.github.ainr.botbek.conf.Config
import org.github.ainr.botbek.tg.bot.BotBek
import org.http4s.blaze.client.BlazeClientBuilder

object Main extends IOApp {

  val bot = BlazeClientBuilder[IO]
    .resource
    .use { httpClient =>
      Config
        .make[IO]()
        .flatMap(config => BotBek.make[IO](config.telegram, httpClient).start())
    }

  override def run(args: List[String]): IO[ExitCode] =
    bot.as(ExitCode.Success)
}
