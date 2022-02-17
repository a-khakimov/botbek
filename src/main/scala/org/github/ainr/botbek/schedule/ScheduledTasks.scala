package org.github.ainr.botbek.schedule

import cats.effect.Sync
import cats.syntax.all._
import cats.{Applicative, Monad}
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.infrastructure.syntax.schedulerSyntax
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.service.{Statistics, UnsplashService}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.syntax.LoggerInterpolator

import java.time.{LocalTime, ZoneId}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait ScheduledTasks[F[_]] {
  def run: F[Unit]
}

object ScheduledTasks {

  def apply[F[_]: Applicative: Monad: Sync: Timer: Logger](
      unsplashService: UnsplashService[F],
      tgBot: BotBek[F]
  ): ScheduledTasks[F] = {

    new ScheduledTasks[F] {

      override def run: F[Unit] = tasks.flatMap {
        tasks =>
          tasks.traverse {
            task =>
              checkTaskTime(task).flatMap {
                case isMomentHasCome if isMomentHasCome =>
                  for {
                    _ <- info"Run for ${task.time}"
                    _ <- tgBot.sendMessage(
                      chatId = task.chatId,
                      text =
                        s"Привет! Наступило время ${task.time} для загрузки статистики с unsplash."
                    )
                    stats <- unsplashService.getUserStatistics(task.user)
                    _ <- info"Stats $stats"
                    _ <- tgBot.sendMessage(
                      chatId = task.chatId,
                      text = s"""|У ${stats.username} сегодня
                             |скачиваний: ${getDownloadsFromStatistics(stats)}
                             |просмотров: ${getViewsFromStatistics(stats)}
                             |""".stripMargin
                    )
                  } yield ()
                case _ => ().pure[F]
              }
          }
      }.as(()).recover {
        case error => error"Recovering error - $error"
      }.every {
        1 minute
      }

      private def getViewsFromStatistics(statistics: Statistics): String = {
        statistics.views.historical.values.reverse.headOption
          .map(_.value.toString)
          .getOrElse("неизвестно")
      }

      private def getDownloadsFromStatistics(statistics: Statistics): String = {
        statistics.downloads.historical.values.reverse.headOption
          .map(_.value.toString)
          .getOrElse("неизвестно")
      }

      private def checkTaskTime(task: Task): F[Boolean] = for {
        now <- Sync[F].delay(LocalTime.now(ZoneId.of("Asia/Yekaterinburg")))
        _ <- info"Check time: task - ${task.time}, now - $now"
        result =
          task.time.isAfter(now) && task.time.isBefore(now.plusMinutes(1))
      } yield result
    }
  }
}
