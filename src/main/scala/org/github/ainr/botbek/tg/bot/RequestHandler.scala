package org.github.ainr.botbek.tg.bot

import cats.FlatMap
import cats.syntax.all.*
import org.github.ainr.botbek.infrastructure.context.Context
import org.github.ainr.botbek.infrastructure.logger.ContextLogger
import telegramium.bots.{ChatIntId, Message}
import telegramium.bots.client.Method
import telegramium.bots.high.Methods

trait RequestHandler[F[_]]:
  def handle(message: Message): Context[F[Method[Message]]]

object RequestHandler:
  def apply[F[_]: FlatMap](
      logger: ContextLogger[F]
  ): RequestHandler[F] = new RequestHandler[F] {

    override def handle(message: Message): Context[F[Method[Message]]] =
      for
        _ <- logger.info(s"Message from ${message.chat.id} ${message.text}")
        method = Methods.sendMessage(
          chatId = ChatIntId(message.chat.id),
          text = s"Привет, брат. Да, я работаю."
        )
      yield method
  }
