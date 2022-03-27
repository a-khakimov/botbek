package org.github.ainr.botbek.infrastructure

import scala.concurrent.duration.FiniteDuration

trait Timer[F[_]] {
  def sleep(duration: FiniteDuration): F[Unit]
}

object Timer {
  def apply[F[_]](
      implicit
      timer: Timer[F]
  ): Timer[F] = timer
}
