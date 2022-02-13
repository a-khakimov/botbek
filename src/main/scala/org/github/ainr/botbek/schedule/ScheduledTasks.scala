package org.github.ainr.botbek.schedule

import cats.effect.Sync
import cats.syntax.all._
import cats.{Applicative, Monad}
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.infrastructure.syntax.schedulerSyntax
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.service.{Statistics, UnsplashService}

import java.time.LocalTime
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait ScheduledTasks[F[_]] {
  def run: F[Unit]
}

object ScheduledTasks {

  def apply[F[_]: Applicative: Monad: Sync: Timer](
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
                    _ <- Sync[F].delay(println(s"Run for ${task.time}"))
                    _ <- tgBot.sendMessage(
                      chatId = task.chatId,
                      text = "Пидр: ${task.time}"
                    )
                    // stats <- unsplashService.getUserStatistics(task.user)
                    // _ <- Sync[F].delay(println(s"Stats ${stats}"))
                    // _ <- tgBot.sendMessage(
                    //  chatId = task.chatId,
                    //  text = s"""|У ${stats.username} сегодня
                    //         |скачиваний: ${getDownloadsFromStatistics(stats)}
                    //         |просмотров: ${getViewsFromStatistics(stats)}
                    //         |""".stripMargin
                    // )
                  } yield ()
                case _ => ().pure[F]
              }
          }
      }.as(()).recover {
        case error => Sync[F].delay(println(s"Recovering error - $error"))
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
        now <- Sync[F].delay(LocalTime.now())
        _ <-
          Sync[F].delay(println(s"Check time: task - ${task.time}, now - $now"))
        result =
          task.time.isAfter(now) && task.time.isBefore(now.plusMinutes(1))
      } yield result
    }
  }
}
