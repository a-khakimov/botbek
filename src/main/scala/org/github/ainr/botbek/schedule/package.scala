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

  def tasks[F[_]: Applicative]: F[List[Task]] = List(
    Task("ainr", ChatIntId(174861972), LocalTime.of(0, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(1, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(2, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(3, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(4, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(5, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(6, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(7, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(8, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(9, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(10, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(11, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(12, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(13, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(14, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(15, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(16, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(17, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(18, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(19, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(20, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(21, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(22, 0)),
    Task("ainr", ChatIntId(174861972), LocalTime.of(23, 0))
  ).pure[F]
}
