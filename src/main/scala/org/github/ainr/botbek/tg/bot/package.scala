package org.github.ainr.botbek.tg

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import telegramium.bots.ChatIntId

import java.time.LocalTime

package object bot {

  case class Task(
      user: String,
      chatId: ChatIntId,
      time: LocalTime
  )

  def tasks[F[_]: Applicative]: F[List[Task]] = List(
    Task("ainr", ChatIntId(174861972), LocalTime.of(9, 0))
  ).pure[F]

}
