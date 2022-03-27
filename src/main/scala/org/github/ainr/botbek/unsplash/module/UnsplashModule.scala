package org.github.ainr.botbek.unsplash.module

import cats.Applicative
import cats.effect.kernel.Concurrent
import org.github.ainr.botbek.unsplash.conf.UnsplashConfig
import org.github.ainr.botbek.unsplash.repo.UnsplashRepository
import org.github.ainr.botbek.unsplash.service.UnsplashService
import org.http4s.client.Client

trait UnsplashModule[F[_]]:
  def unsplashService: UnsplashService[F]

object UnsplashModule:

  def apply[F[_]: Applicative: Concurrent](
      conf: UnsplashConfig,
      httpClient: Client[F]
  ): UnsplashModule[F] = new UnsplashModule[F] {

    val unsplashRepository: UnsplashRepository[F] = UnsplashRepository[F](
      conf,
      httpClient
    )

    override def unsplashService: UnsplashService[F] =
      UnsplashService(unsplashRepository)
  }
