package Util.File
import java.nio.charset.StandardCharsets

import cats.implicits._
import monix.eval.Task

import scala.io.Source
import scala.reflect.io.{Directory, File, Path}
import Util.Logging.log

object FileHelper {
  /** Gets csv files
    *
    * @param dir to extract files from
    * @param skipFiles that should be skipped when building up the list
    * @return
    */
  def getCsvFiles(dir: Directory, skipFiles: List[File] = List.empty[File]): Task[Either[Exception, List[File]]] = {
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
  def extractCsvFileLines(file: File): Task[Either[Exception, List[String]]] = Task{
    try {
      val reader = Source.fromFile(file.toURI, StandardCharsets.ISO_8859_1.name())
      Right(reader.getLines().toList)
    } catch {
      case e: Exception => Left(new Exception("Failed to extract csv files: "+ e.getMessage, e))
    }
  }

  /** finds a file in a directory
    *
    * @param dir to the file
    * @param fileName to find by
    * @return optional found file
    */
  def findFile(dir: Directory, fileName: String): Task[Either[Exception, File]] = Task {
    Either.fromOption(dir.files.toList.find(_.name == fileName), new Exception("File not found: "+ fileName))
  }

  /** Casts a string to a url
    *
    * @param dir string to cast from
    * @return converted Directory
    */
  def toDirectory(dir: String): Either[Exception, Directory] = {
    try{
      val directory = File(Path(dir))
      Right(Directory(directory))
    } catch {
      case e: Exception => Left(new Exception(s"Could not convert to Directory $dir", e))
    }
  }

}
