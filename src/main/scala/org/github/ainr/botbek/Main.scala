package org.github.ainr.botbek

import cats.Parallel
import cats.effect.{Async, ExitCode, IO, IOApp}
import cats.syntax.all._
import org.http4s.blaze.client.BlazeClientBuilder
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatIntId, Message}

object Main extends IOApp {

  val token: String = ???

  val bot = BlazeClientBuilder[IO].resource.use { httpClient =>
    implicit val api: Api[IO] =
      BotApi(httpClient, baseUrl = s"https://api.telegram.org/bot$token")
    val bot = new MyLongPollBot[IO]()
    bot.start()
  }

  override def run(args: List[String]): IO[ExitCode] = bot.as(ExitCode.Success)
}

class MyLongPollBot[F[_]: Async: Parallel]()(
    implicit
    api: Api[F]
) extends LongPollBot[F](api) {

  override def onMessage(msg: Message): F[Unit] =
    Methods
      .sendMessage(
        chatId = ChatIntId(msg.chat.id),
        text = "Salom Dunyo!"
      )
      .exec.void
}
