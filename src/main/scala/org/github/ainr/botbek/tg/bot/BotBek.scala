package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all.*
import org.github.ainr.botbek.infrastructure.context.withContext
import org.github.ainr.botbek.infrastructure.logger.ContextLogger
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.http4s.client.Client
import telegramium.bots
import telegramium.bots.client.Method
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, LongPollBot, Methods}
import telegramium.bots.{ChatId, IFile, Message}

import scala.language.postfixOps

trait BotBek[F[_]]:

  def sendMessage[Message](chatId: ChatId, text: String): F[bots.Message]

  def sendPhoto(chatId: ChatId, photo: IFile): F[bots.Message]

object BotBek:

  def apply[F[_]: Async: Parallel](
      api: Api[F],
      httpClient: Client[F]
  )(
      requestHandler: RequestHandler[F]
  ): LongPollBot[F] with BotBek[F] = {

    new LongPollBot[F](api) with BotBek[F]:

      override def onMessage(message: Message): F[Unit] = withContext {
        requestHandler
          .handle(message)
          .flatMap(_.exec(api))
          .as(())
      }

      override def sendMessage[Message](
          chatId: ChatId,
          text: String
      ): F[bots.Message] =
        Methods.sendMessage(
          chatId = chatId,
          text = text
        ).exec(api)

      override def sendPhoto(chatId: ChatId, photo: IFile): F[Message] =
        Methods.sendPhoto(
          chatId = chatId,
          photo = photo
        ).exec(api)
  }
