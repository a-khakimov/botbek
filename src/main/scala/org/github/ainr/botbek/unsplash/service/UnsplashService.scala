package org.github.ainr.botbek.unsplash.service

import cats.Functor
import cats.syntax.all._
import org.github.ainr.botbek.unsplash.repo.UnsplashRepository

trait UnsplashService[F[_]]:
  def getUserStatistics(user: String): F[Statistics]

object UnsplashService:

  def apply[F[_]: Functor](
      repo: UnsplashRepository[F]
  ): UnsplashService[F] = new UnsplashService[F]() {
    override def getUserStatistics(user: String): F[Statistics] = {
      repo
        .getUserStatistics(user)
        .map(Statistics.apply)
    }
  }
