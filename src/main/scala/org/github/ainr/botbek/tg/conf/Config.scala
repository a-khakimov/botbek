package org.github.ainr.botbek.tg.conf

import cats.effect.Sync
import cats.syntax.all._
import pureconfig.error.ConfigReaderException
import pureconfig.generic.semiauto.deriveConvert
import pureconfig.{ConfigConvert, ConfigSource}

final case class Config(
    token: String
)

object Config {

  implicit private val convert: ConfigConvert[Config] = deriveConvert

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
