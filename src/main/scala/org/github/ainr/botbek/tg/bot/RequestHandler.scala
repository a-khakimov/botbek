package org.github.ainr.botbek.tg.bot

import cats.effect.Sync
import cats.syntax.all.*
import org.github.ainr.botbek.BuildInfo
import org.github.ainr.botbek.infrastructure.context.Context
import org.github.ainr.botbek.infrastructure.logger.ContextLogger
import telegramium.bots.{ChatId, ChatIntId, Message}
import telegramium.bots.client.Method
import telegramium.bots.high.Methods

import java.text.SimpleDateFormat

trait RequestHandler[F[_]]:
  def handle(message: Message): Context[F[Method[Message]]]

object RequestHandler:
  def apply[F[_]: Sync](
      logger: ContextLogger[F]
  ): RequestHandler[F] = new RequestHandler[F] {

    private def sendVersion(id: ChatId): Context[F[Method[Message]]] =
      Sync[F].delay {
        Methods.sendMessage(
          chatId = id,
          text =
            s"""${BuildInfo.name} ${BuildInfo.version}
               |Commit: ${BuildInfo.gitHeadCommit.getOrElse("")}
               |Build time: ${
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                  .format(BuildInfo.buildTime)
              }
               |""".stripMargin
        )
      } <* logger.info(s"Send BuildInfo $BuildInfo")

    private def defaultResponse(id: ChatId): Context[F[Method[Message]]] =
      Sync[F].delay {
        Methods.sendMessage(
          chatId = id,
          text = s"Привет, брат. Да, я работаю."
        )
      } <* logger.info(s"Default response")

    private def commands(
        message: Message
    ): Context[F[Method[Message]]] = message.text match {
      case Some("/version") => sendVersion(ChatIntId(message.chat.id))
      case other            => defaultResponse(ChatIntId(message.chat.id))
    }

    override def handle(message: Message): Context[F[Method[Message]]] =
      for
        _ <- logger.info(s"Message from ${message.chat.id} ${message.text}")
        response <- commands(message)
      yield response
  }
