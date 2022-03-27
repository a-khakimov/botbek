package org.github.ainr.botbek.infrastructure.logger

import org.github.ainr.botbek.infrastructure.context.{Context, RequestContext}
import org.typelevel.log4cats.Logger

trait ContextLogger[F[_]]:

  def error(message: => String): Context[F[Unit]]

  def warn(message: => String): Context[F[Unit]]

  def info(message: => String): Context[F[Unit]]

  def debug(message: => String): Context[F[Unit]]

  def trace(message: => String): Context[F[Unit]]

  def error(throwable: Throwable)(message: => String): Context[F[Unit]]

  def warn(throwable: Throwable)(message: => String): Context[F[Unit]]

  def info(throwable: Throwable)(message: => String): Context[F[Unit]]

  def debug(throwable: Throwable)(message: => String): Context[F[Unit]]

  def trace(throwable: Throwable)(message: => String): Context[F[Unit]]

object ContextLogger:

  def apply[F[_]](logger: Logger[F]): ContextLogger[F] = new ContextLogger[F] {

    private def contextual(message: => String): Context[String] =
      s"${summon[RequestContext].requestId} - $message"

    def error(message: => String): Context[F[Unit]] =
      logger.error(contextual(message))

    def warn(message: => String): Context[F[Unit]] =
      logger.warn(contextual(message))

    def info(message: => String): Context[F[Unit]] =
      logger.info(contextual(message))

    def debug(message: => String): Context[F[Unit]] =
      logger.debug(contextual(message))

    def trace(message: => String): Context[F[Unit]] =
      logger.trace(contextual(message))

    def error(throwable: Throwable)(message: => String): Context[F[Unit]] =
      logger.error(throwable)(contextual(message))

    def warn(throwable: Throwable)(message: => String): Context[F[Unit]] =
      logger.warn(throwable)(contextual(message))

    def info(throwable: Throwable)(message: => String): Context[F[Unit]] =
      logger.info(throwable)(contextual(message))

    def debug(throwable: Throwable)(message: => String): Context[F[Unit]] =
      logger.debug(throwable)(contextual(message))

    def trace(throwable: Throwable)(message: => String): Context[F[Unit]] =
      logger.trace(throwable)(contextual(message))
  }
