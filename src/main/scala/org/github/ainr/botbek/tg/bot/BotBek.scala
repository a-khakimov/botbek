package org.github.ainr.botbek.tg.bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all._
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.github.ainr.botbek.unsplash.service.UnsplashService
import org.http4s.client.Client
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, LongPollBot, Methods}
import telegramium.bots.{ChatId, ChatIntId, Message}
import org.github.ainr.botbek.infrastructure.syntax.schedulerSyntax

import java.time.LocalTime
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

final class BotBek[
    F[_]: Async: Parallel: Timer
](
    api: Api[F]
)(
    unsplashService: UnsplashService[F]
) extends LongPollBot[F](api) {

  def runScheduledTasks(): F[List[Unit]] = tasks.flatMap {
    tasks =>
      val now = LocalTime.now()
      tasks.traverse { task =>
        if (task.time.isAfter(now) && task.time.isBefore(now.plusMinutes(1)))
          sendUnsplashStat(task.chatId, task.user)
        else ().pure[F]
      }
  }
    .every(1 minute)

  private def sendUnsplashStat(chatId: ChatId, unsplashUser: String): F[Unit] =
    for {
      stat <- unsplashService.getUserStatistics(unsplashUser)
      lastDayDownloads =
        stat.downloads.historical.values.reverse.headOption.map(
          _.value.toString
        ).getOrElse("неизвестно")
      lastDayViews = stat.views.historical.values.reverse.headOption.map(
        _.value.toString
      ).getOrElse("неизвестно")
      _ <- Methods
        .sendMessage(
          chatId = chatId,
          text =
            s"""
            |У ${stat.username} сегодня
            |скачиваний: $lastDayDownloads
            |просмотров: $lastDayViews
            |""".stripMargin
        )
        .exec(api)
    } yield ()

  override def onMessage(msg: Message): F[Unit] = for {
    _ <- Methods
      .sendMessage(
        chatId = ChatIntId(msg.chat.id),
        text = s"Привет, брат."
      )
      .exec(api)
  } yield ()
}

object BotBek {

  def make[
      F[_]: Async: Parallel: Timer
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
