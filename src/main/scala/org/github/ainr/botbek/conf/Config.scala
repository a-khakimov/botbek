package org.github.ainr.botbek.conf

import cats.effect.Async
import cats.syntax.all.*
import org.github.ainr.botbek.tg.conf.TelegramConfig
import org.github.ainr.botbek.unsplash.conf.UnsplashConfig
import ciris.*
import lt.dvim.ciris.Hocon._
import com.typesafe.config.ConfigFactory

final case class Config(
    telegram: TelegramConfig,
    unsplash: UnsplashConfig
)

object Config:
  def load[F[_]: Async]: F[Config] = {

    val config = ConfigFactory.load("reference.conf")
    val telegram = hoconAt(config)("telegram")
    val unsplash = hoconAt(config)("unsplash")

    val telegramConfig: ConfigValue[Effect, TelegramConfig] = (
      telegram("url").as[String],
      telegram("token").as[String]
    ).parMapN(TelegramConfig.apply)

    val unsplashConfig: ConfigValue[Effect, UnsplashConfig] = (
      unsplash("url").as[String],
      unsplash("token").as[String]
    ).parMapN(UnsplashConfig.apply)

    (telegramConfig, unsplashConfig)
      .parMapN(Config.apply)
      .load
  }
