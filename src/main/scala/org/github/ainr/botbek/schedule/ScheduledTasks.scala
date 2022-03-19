package org.github.ainr.botbek.schedule

import cats.effect.Sync
import cats.syntax.all.*
import cats.{Applicative, Monad}
import org.github.ainr.botbek.infrastructure.Timer
import org.github.ainr.botbek.infrastructure.syntax.schedulerSyntax
import org.github.ainr.botbek.tg.bot.BotBek
import org.github.ainr.botbek.unsplash.service.{Statistics, UnsplashService}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.nspl.*
import org.nspl.awtrenderer.*
import telegramium.bots.{ChatIntId, InputPartFile}

import java.time.{LocalTime, ZoneId}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait ScheduledTasks[F[_]] {
  def run: F[Unit]
}

object ScheduledTasks:

  def apply[F[_]: Applicative: Monad: Sync: Timer: Logger](
      unsplashService: UnsplashService[F],
      tgBot: BotBek[F]
  ): ScheduledTasks[F] = new ScheduledTasks[F] {

    override def run: F[Unit] =
      repeated {
        recovered {
          for
            tasks <- tasksF
            _ <- tasks.traverse {
              task =>
                checkTaskTime(task).flatMap {
                  case moment: Boolean if moment => runTask(task)
                  case _                         => ().pure[F]
                }
            }
          yield ()
        }
      }

    private def runTask(task: Task): F[Unit] =
      for
        _ <- info"Run for ${task.time}"
        stats <- unsplashService.getUserStatistics(task.user)
        _ <- info"Stats $stats"
        _ <- tgBot.sendMessage(
          chatId = task.chatId,
          text = s"""
                    |Настало время для загрузки статистики с unsplash, брат.
                    |У ${stats.username} за последние сутки было
                    |скачиваний: ${getLastDownloadsFromStatistics(stats)}
                    |просмотров: ${getLastViewsFromStatistics(stats)}
                    |""".stripMargin
        )
        _ <- sendAllStats(task.chatId, stats)
      yield ()

    private def repeated(f: F[Unit]): F[Unit] = f.every(1 minute)

    private def recovered(f: F[Unit]): F[Unit] = f.recover {
      case cause => error"Recovered from error: $cause"
    }

    private def getLastViewsFromStatistics(statistics: Statistics): String =
      statistics.views.historical.values.reverse.headOption
        .map(_.value.toString)
        .getOrElse("неизвестно")

    private def getLastDownloadsFromStatistics(statistics: Statistics): String =
      statistics.downloads.historical.values.reverse.headOption
        .map(_.value.toString)
        .getOrElse("неизвестно")

    // refach it
    private def sendAllStats(
        chatId: ChatIntId,
        statistics: Statistics
    ): F[Unit] = {

      val viewsPlot = getPlot(
        statistics.views.historical.values.map(_.value.toDouble),
        "Views",
        Color.blue
      )

      val downloadsPlot = getPlot(
        statistics.downloads.historical.values.map(_.value.toDouble),
        "Downloads",
        Color.red
      )

      for
        plots <- Sync[F].delay(sequence(
          viewsPlot :: downloadsPlot :: Nil,
          TableLayout(2)
        ))
        file <- Sync[F].delay(renderToFile(plots, width = 2000))
        _ <- tgBot.sendPhoto(chatId, InputPartFile(file))
      yield ()
    }

    private def getPlot(
        data: Seq[Double],
        main: String,
        color: Color
    ): Elems2[XYPlotArea, Legend] = xyplot(
      data -> line(
        stroke = StrokeConf(RelFontSize(0.5)),
        color = color
      )
    )(main = main).build

    private def checkTaskTime(task: Task): F[Boolean] =
      for
        now <- Sync[F].delay(LocalTime.now(ZoneId.of("Asia/Yekaterinburg")))
        _ <- info"Check time: task - ${task.time}, now - $now"
        result =
          task.time.isAfter(now) && task.time.isBefore(now.plusMinutes(1))
      yield result
  }
