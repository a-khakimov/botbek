package org.github.ainr.botbek.unsplash.repo

import cats.data.EitherT
import cats.effect.Concurrent
import cats.syntax.all._
import cats.{Applicative, MonadError}
import io.circe.generic.auto._
import org.github.ainr.botbek.unsplash.conf.UnsplashConfig
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.headers.{Accept, Authorization}

import scala.language.implicitConversions

trait UnsplashRepository[F[_]] {
  def getUserStatistics(user: String): F[RawStatistics]
}

object UnsplashRepository {

  def apply[F[_]: Applicative: Concurrent](
      conf: UnsplashConfig,
      httpClient: Client[F]
  )(
      implicit
      F: MonadError[F, Throwable]
  ): UnsplashRepository[F] = {

    lazy val auth = Authorization(
      Credentials.Token(
        AuthScheme.Bearer,
        conf.token
      )
    )

    val mediaTypeJson = Accept(MediaType.application.json)
    val request = Request[F]().putHeaders(auth, mediaTypeJson)

    new UnsplashRepository[F] {
      override def getUserStatistics(user: String): F[RawStatistics] = for {
        uri <- EitherT
          .fromEither[F](Uri.fromString(s"${conf.url}/users/$user/statistics"))
          .rethrowT
        statistics <-
          httpClient.expect(request.withUri(uri))(jsonOf[F, RawStatistics])
      } yield statistics
    }
  }
}
