package org.github.ainr.botbek.infrastructure

import cats.effect.Sync

package object context {

  type Context[T] = RequestContext ?=> T

  final case class RequestContext(
      requestId: String
  )

  def withContext[F[_]: Sync, T](f: Context[F[T]]): F[T] = {
    f(using RequestContext (
      requestId = java.util.UUID.randomUUID.toString
    ))
  }
}
