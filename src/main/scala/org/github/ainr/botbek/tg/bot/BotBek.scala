package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.{Async, Sync}
import cats.syntax.all.*
import cats.effect.*
import cats.implicits.*
import cats.{Monad, MonadThrow, ~>}
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.http4s.client.Client
import telegramium.bots
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatId, ChatIntId, IFile, Message}

import scala.language.postfixOps

trait BotBek[F[_]] {

  def sendMessage[Message](chatId: ChatId, text: String): F[bots.Message]

  def sendPhoto(chatId: ChatId, photo: IFile): F[bots.Message]
}

object BotBek:

  object context {
    type Contextual[T] = RequestContext ?=> T
    final case class RequestContext(requestId: String)

    object RequestContextual:
      def withContext[F[_]: Sync](f: Contextual[F[Unit]]): F[Unit] = {
        val context: RequestContext =
          RequestContext(java.util.UUID.randomUUID.toString)
        f(
          using
          context
        )
      }

    import org.typelevel.log4cats.Logger

    class ContextualLogger[F[_]](logger: Logger[F]):

      private def contextual(message: => String): Contextual[String] =
        s"${summon[RequestContext].requestId} - $message"

      def error(message: => String): Contextual[F[Unit]] =
        logger.error(contextual(message))
      def warn(message: => String): Contextual[F[Unit]] =
        logger.warn(contextual(message))
      def info(message: => String): Contextual[F[Unit]] =
        logger.info(contextual(message))
      def debug(message: => String): Contextual[F[Unit]] =
        logger.debug(contextual(message))
      def trace(message: => String): Contextual[F[Unit]] =
        logger.trace(contextual(message))
  }

  def make[F[_]: Async: Parallel](
      conf: TelegramConfig,
      httpClient: Client[F]
  ): LongPollBot[F] with BotBek[F] = {

    import context._
    import context.RequestContextual._

    import org.typelevel.log4cats.slf4j.Slf4jLogger
    val logger: ContextualLogger[F] =
      new ContextualLogger(Slf4jLogger.getLogger[F])

    val api: Api[F] = BotApi(
      http = httpClient,
      baseUrl = s"${conf.url}/bot${conf.token}"
    )

    new LongPollBot[F](api) with BotBek[F]:

      def handle(message: Message): Contextual[F[Unit]] = {
        for
          _ <- logger.info(s"Message from ${message.chat.id}")
          _ <- Methods.sendMessage(
            chatId = ChatIntId(message.chat.id),
            text = s"Привет, брат. Да, я работаю."
          ).exec(api)
        yield ()
      }

      override def onMessage(message: Message): F[Unit] = withContext {
        handle(message)
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
