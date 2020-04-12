package Util

import Util.Logging.log
import monix.eval.Task

case object ErrorHandler {
 def errorT(message: String, e:Exception): Task[Left[Exception, Nothing]] = {
   log.error(message)
   Task.now(Left(new Exception(message, e)))
 }

  def errorT(message: String): Task[Left[Exception, Nothing]] = {
    log.error(message)
    Task.now(Left(new Exception(message)))
  }

  def errorL(message: String): Left[Exception, Nothing] = {
    log.error(message)
    Left(new Exception(message))
  }

  def errorL(message: String, e: Exception): Left[Exception, Nothing] = {
    log.error(message)
    Left(new Exception(message, e))
  }

  def error(message: String): Exception = {
    log.error(message)
    new Exception(message)
  }

  def error(message: String, e: Exception): Exception = {
    log.error(message)
    new Exception(message, e)
  }

  def error(exception: Exception): Exception = {
    log.error(exception.getMessage)
    exception
  }
}
