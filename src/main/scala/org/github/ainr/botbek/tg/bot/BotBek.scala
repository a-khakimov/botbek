package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.{Async, Sync}
import cats.syntax.all._
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.http4s.client.Client
import telegramium.bots
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatId, ChatIntId, Message}

import java.time.{LocalDateTime, LocalTime, ZoneId}
import java.util.TimeZone
import scala.language.postfixOps

trait BotBek[F[_]] {
  def sendMessage[Message](chatId: ChatId, text: String): F[bots.Message]
}

object BotBek {

  def make[F[_]: Async: Parallel: Timer](
      conf: TelegramConfig,
      httpClient: Client[F]
  ): LongPollBot[F] with BotBek[F] = {
    val api: Api[F] = BotApi(
      httpClient,
      baseUrl = s"${conf.url}/bot${conf.token}"
    )
    new LongPollBot[F](api) with BotBek[F] {

      override def onMessage(msg: Message): F[Unit] = {
        Methods.sendMessage(
          chatId = ChatIntId(msg.chat.id),
          text = s"Привет, брат. Да, я работаю."
        ).exec(api).as(())
      }

      def sendMessage[Message](
          chatId: ChatId,
          text: String
      ): F[bots.Message] = {
        Methods.sendMessage(
          chatId = chatId,
          text = text
        ).exec(api)
      }
    }
  }
}
