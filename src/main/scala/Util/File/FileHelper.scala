package Util.File
import java.nio.charset.StandardCharsets

import cats.implicits._
import monix.eval.Task

import scala.io.Source
import scala.reflect.io.{Directory, File, Path}
import Util.Logging.log
import Util.ErrorHandler._

object FileHelper {
  /** Gets csv files
    *
    * @param dir to extract files from
    * @param skipFiles that should be skipped when building up the list
    * @return
    */
  def getCsvFiles(dir: Directory, skipFiles: List[File] = List.empty[File]): Task[Either[Exception, List[File]]] = {
    log.debug("Skipping files that are not csv items")
    Task {
      val okFileExtensions = List("csv")
      Right(dir.files.filter(file => okFileExtensions.contains(file.extension)).toList)
    }.onErrorHandle { e =>
        val message = s"Could not read files from path: ${e.getMessage}"
        log.error(message, e)
        Left(new Exception(message))
      }
  }

  /** Extracts the csv file content
    *
    * @param file to extract from
    * @return line items
    */
  def extractCsvFileLines(file: File): Task[Either[Exception, List[String]]] = Task {

    try {
      log.debug("Reading in file")
      val reader = Source.fromFile(file.toURI, StandardCharsets.ISO_8859_1.name())
      Right(reader.getLines().toList)
    } catch {
      case e: Exception =>
        val message = "Failed to extract csv files: "+ e.getMessage
        log.error(message)
        Left(new Exception(message, e))
    }
  }

  /** finds a file in a directory
    *
    * @param dir to the file
    * @param fileName to find by
    * @return optional found file
    */
  def findFile(dir: Directory, fileName: String): Task[Either[Exception, File]] = Task {
    log.debug("Finding file by name")
    Either.fromOption(dir.files.toList.find(_.name == fileName), {
      val error = new Exception("File not found: " + fileName)
      log.error(error.getMessage)
      error
    })
  }

  /** Casts a string to a url
    *
    * @param dir string to cast from
    * @return converted Directory
    */
  def toDirectory(dir: String): Either[Exception, Directory] = {
    try{
      log.debug("Build directory from path string")
      val directory = File(Path(dir))
      Right(Directory(directory))
    } catch {
      case e: Exception =>
        errorL(s"Could not convert to Directory $dir", e)
    }
  }

}
