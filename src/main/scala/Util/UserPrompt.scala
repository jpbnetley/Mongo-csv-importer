package Util

import Util.File.FileHelper
import Util.Models.UserInput

object UserPrompt {

  /** Prompts a user for input
    *
    * @return UserInput object
    */
  def promptUser(): Either[Exception, UserInput] = {
    try {
      println("Please enter the path to the csv files: ")
      val uriInput = FileHelper.toUri(scala.io.StdIn.readLine()).fold(throw _, identity)
      val skipItems = addSkipItems(List.empty[String]).flatMap(FileHelper.findFile(uriInput, _))

      Right(UserInput(uriInput, skipItems))
    } catch {
      case e: Exception =>
        val message = s"Could not read user input: ${e.getMessage}"
        Left(new Exception(message))
    }
  }

  /** Prompts the user to add files to skip
    *
    * @param inputItems
    * @return skipped items list
    */
  def addSkipItems(inputItems: List[String]): List[String] = {
    println("Please enter any files to skip (enter :q to exit) eg. filename.extension: ")
    val input = scala.io.StdIn.readLine()
    if (!inputItems.lastOption.contains(":q"))
      inputItems.drop(inputItems.length - 1)
    else addSkipItems(inputItems :+ input)
  }

}
