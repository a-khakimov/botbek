package org.github.ainr.botbek

import cats.syntax.all._
import cats.Applicative
import telegramium.bots.ChatIntId

import java.time.LocalTime

package object schedule {

  case class Task(
      user: String,
      chatId: ChatIntId,
      time: LocalTime
  )

  // yep. this is hard code, bro
  def tasksF[F[_]: Applicative]: F[List[Task]] = List(
    Task("ainr", ChatIntId(174861972), LocalTime.of(19, 36))
  ).pure[F]
}
