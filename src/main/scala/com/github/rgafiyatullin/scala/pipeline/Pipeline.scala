package com.github.rgafiyatullin.scala.pipeline

import scala.concurrent.{ExecutionContext, Future}


abstract class Pipeline[+I, +O, E] {
  self =>
  type ProcessF[-I, +O, E] = Function[I, Result[O, E]]

  def apply(): Result[O, E]

  def ->[NextO](process: ProcessF[O, NextO, E]): Pipeline[O, NextO, E] =
    new Pipeline[O, NextO, E] {
      def apply(): Result[NextO, E] = self.apply() match {
        case Ok(intermediate) =>
          process(intermediate)
        case Err(e) =>
          Err(e)
      }
    }
}

object Pipeline {
  def fromResult[R, E](result: Result[R, E]) =
    new Pipeline[Nothing, R, E] {
      def apply(): Result[R, E] = result
    }

  def fromValue[I, E](value: I): Pipeline[Nothing, I, E] =
    new Pipeline[Nothing, I, E] {
      def apply(): Result[I, E] = Ok(value)
    }

  def fromUnit[E]: Pipeline[Nothing, Unit, E] =
    fromValue[Unit, E]( () )
}
