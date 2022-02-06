package org.github.ainr.botbek.conf

import cats.effect.Sync
import cats.syntax.all._
import org.github.ainr.botbek.tg.conf.TelegramConfig
import pureconfig.{ConfigConvert, ConfigSource}
import pureconfig.error.ConfigReaderException
import pureconfig.generic.semiauto.deriveConvert

final case class Config(
    telegram: TelegramConfig
)

object Config {

  implicit private val convertTelegramConfig: ConfigConvert[TelegramConfig] =
    deriveConvert
  implicit private val convertConfig: ConfigConvert[Config] = deriveConvert

  def make[F[_]: Sync](): F[Config] =
    Sync[F].delay {
      ConfigSource
        .default
        .load[Config]
    }
      .flatMap {
        case Left(error) =>
          Sync[F]
            .raiseError(new ConfigReaderException[Config](error))
        case Right(config) => config.pure[F]
      }
}
