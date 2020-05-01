package Util

import Util.file.FileHelper
import Util.models.UserInput
import cats.data.EitherT
import cats.implicits._
import monix.eval.Task
import Util.Logging.log
import Util.ErrorHandler._

object UserPrompt {

  /** Prompts a user for input
    *
    * @return UserInput object
    */
  def promptUser(): Task[Either[Exception, UserInput]] = {
    try {
      log.debug("Prompting user for input...")
      println("Please enter the path to the csv files: ")
      (for {
        directory <- EitherT.fromEither[Task](FileHelper.toDirectory(scala.io.StdIn.readLine()))
        skipItems <- EitherT.fromEither[Task](addSkipItems(List.empty[String]))
        files     <- EitherT(Task.parTraverse(skipItems)(FileHelper.findFile(directory, _)).map(_.sequence))
      } yield UserInput(directory, files)).value

    } catch {
      case e: Exception =>
        val message = s"Could not read user input"
        errorT(message, e)
    }
  }

  /** Prompts the user to add files to skip
    *
    * @param inputItems the items that will skipped
    * @return skipped items list
    */
  def addSkipItems(inputItems: List[String]): Either[Exception, List[String]] = {
    try {
      log.debug("Prompting user to enter files to skip")
      println("Please enter any files to skip (enter :q to exit) eg. filename.extension: ")
      val input = scala.io.StdIn.readLine()
      if (input.equals(":q"))
        Right(inputItems)
      else addSkipItems(inputItems :+ input)
    } catch {
      case e: Exception =>
        val message = "Could not read user input: "+e.getMessage
        log.error(message)
        Left(new Exception(message, e))
    }
  }

}
