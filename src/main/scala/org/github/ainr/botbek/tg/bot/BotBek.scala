package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all._
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.github.ainr.botbek.unsplash.service.UnsplashService
import org.http4s.client.Client
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatIntId, Message}

final class BotBek[
    F[_]: Async: Parallel
](
    api: Api[F]
)(
    unsplashService: UnsplashService[F]
) extends LongPollBot[F](api) {

  override def onMessage(msg: Message): F[Unit] = for {
    stat <- unsplashService.getUserStatistics("ainr")
    lastDayDownloads = stat.downloads.historical.values.headOption.map(
      _.value.toString
    ).getOrElse("неизвестно")
    lastDayViews = stat.views.historical.values.headOption.map(
      _.value.toString
    ).getOrElse("неизвестно")
    _ <- Methods
      .sendMessage(
        chatId = ChatIntId(msg.chat.id),
        text =
          s"""
             |У ${stat.username} сегодня
             |скачиваний: $lastDayDownloads
             |просмотров: $lastDayViews
             |""".stripMargin
      )
      .exec(api)
  } yield ()
}

object BotBek {

  def make[
      F[_]: Async: Parallel
  ](
      conf: TelegramConfig,
      httpClient: Client[F]
  )(
      unsplashService: UnsplashService[F]
  ): BotBek[F] = {
    val api: Api[F] = BotApi(
      httpClient,
      baseUrl = s"${conf.url}/bot${conf.token}"
    )
    new BotBek[F](
      api
    )(
      unsplashService
    )
  }
}
