package org.github.ainr.botbek.infrastructure

import cats.MonadThrow
import cats.syntax.all.*

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

final class Scheduler[
    F[_]: Timer: MonadThrow,
    T
](
    val action: F[T]
) {
  def every(duration: FiniteDuration): F[T] = for {
    _ <- action
    _ <- Timer[F].sleep(duration)
    result <- every(duration)
  } yield result
}

object syntax {

  implicit def schedulerSyntax[
      F[_]: Timer: MonadThrow,
      T
  ](
      action: F[T]
  ): Scheduler[F, T] = new Scheduler[F, T](action)
}
