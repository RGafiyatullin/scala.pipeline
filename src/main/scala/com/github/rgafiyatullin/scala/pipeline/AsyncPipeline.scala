package com.github.rgafiyatullin.scala.pipeline

import scala.concurrent.{Future, ExecutionContext}

abstract class AsyncPipeline[+I, +O, E](implicit ec: ExecutionContext) {
  self =>
  type ProcessF[-I, +O, E] = Function[I, Future[Result[O, E]]]

  def apply(): Future[Result[O, E]]

  def ->[NextO](process: ProcessF[O, NextO, E]): AsyncPipeline[O, NextO, E] =
    new AsyncPipeline[O, NextO, E] {
      def apply(): Future[Result[NextO, E]] = self.apply().flatMap {
        case Ok(intermediate) =>
          process(intermediate)
        case Err(e) =>
          Future.successful(Err(e))
      }
    }
}

object AsyncPipeline {
  def fromValue[I, E](futureValue: Future[I])(implicit ec: ExecutionContext): AsyncPipeline[Nothing, I, E] =
    new AsyncPipeline[Nothing, I, E] {
      def apply(): Future[Result[I, E]] =
        futureValue.map { value => Ok(value) }
    }

  def fromResult[R, E](futureResult: Future[Result[R, E]])(implicit ec: ExecutionContext) =
    new AsyncPipeline[Nothing, R, E] {
      def apply(): Future[Result[R, E]] = futureResult
    }

  def fromUnit[E](implicit ec: ExecutionContext) =
    fromValue[Unit, E](Future.successful( () ))
}
