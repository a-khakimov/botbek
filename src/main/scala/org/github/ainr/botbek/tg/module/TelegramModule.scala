package org.github.ainr.botbek.tg.module

import cats.Parallel
import cats.effect.{Async, IO}
import org.github.ainr.botbek.infrastructure.logger.ContextLogger
import org.github.ainr.botbek.tg.bot.{BotBek, RequestHandler}
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.http4s.client.Client
import telegramium.bots.high.{Api, BotApi, LongPollBot}

trait TelegramModule[F[_]]:
  def botBek: LongPollBot[F] & BotBek[F]

object TelegramModule:

  def apply[F[_]: Async: Parallel](
      config: TelegramConfig,
      httpClient: Client[F],
      contextLogger: ContextLogger[F]
  ): TelegramModule[F] = new TelegramModule[F] {

    private val requestHandler: RequestHandler[F] =
      RequestHandler[F](contextLogger)

    private val botApi: Api[F] = BotApi(
      http = httpClient,
      baseUrl = s"${config.url}/bot${config.token}"
    )

    override val botBek: LongPollBot[F] & BotBek[F] = BotBek[F](
      botApi,
      httpClient
    )(
      requestHandler
    )
  }
