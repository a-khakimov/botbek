package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all._
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.http4s.client.Client
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatIntId, Message}

final class BotBek[
    F[_]: Async: Parallel
](
    api: Api[F]
) extends LongPollBot[F](api) {

  override def onMessage(msg: Message): F[Unit] =
    Methods
      .sendMessage(
        chatId = ChatIntId(msg.chat.id),
        text = "Salom Dunyo!"
      )
      .exec(api)
      .void
}

object BotBek {

  def make[
      F[_]: Async: Parallel
  ](
      conf: TelegramConfig,
      httpClient: Client[F]
  ): BotBek[F] = {
    val api: Api[F] = BotApi(
      httpClient,
      baseUrl = s"https://api.telegram.org/bot${conf.token}"
    )
    new BotBek[F](api)
  }
}
