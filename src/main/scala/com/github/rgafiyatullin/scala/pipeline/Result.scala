
package com.github.rgafiyatullin.scala.pipeline

import java.util.NoSuchElementException

sealed abstract class Result[+R, +E] {
  def isOk: Boolean
  def isErr: Boolean = ! isOk
  def ok: R
  def err: E
}

case class Ok[R, E](result: R) extends Result[R, E] {
  def isOk: Boolean = true

  def ok: R = result

  def err: Nothing = throw new NoSuchElementException("Result.err")
}

case class Err[R, E](error: E) extends Result[R, E] {
  def isOk: Boolean = false

  def ok: Nothing = throw new NoSuchElementException("Result.ok")

  def err: E = error
}


