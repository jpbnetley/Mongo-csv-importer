package Util.File
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets

import cats.implicits._
import monix.eval.Task

import scala.io.Source

object FileHelper {
  /** Gets a list of files in a directory
    *
    * @param dir to get the fies from
    * @param extensions to only show
    * @return List[Files]
    */
  def getListOfFiles(dir: File, extensions: List[String]): Task[List[File]] = Task {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  /** Gets csv files
    *
    * @param dir to extract files from
    * @param skipFiles that should be skipped when building up the list
    * @return
    */
  def getCsvFiles(dir: URI, skipFiles: List[File] = List.empty[File]): Task[Either[Exception, List[File]]] = {
    val okFileExtensions = List("csv")
      getListOfFiles(new File(dir.getPath), okFileExtensions).map(files => Right(files.diff(skipFiles))).onErrorHandle { e =>
        val message = s"Could not read files from path: ${e.getMessage}"
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
      val reader = Source.fromFile(file.toPath.toUri, StandardCharsets.ISO_8859_1.name())
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
  def findFile(dir: URI, fileName: String): Task[Either[Exception, File]] = Task {
    Either.fromOption(new File(dir.getPath).listFiles(_.isFile).find(_.getName == fileName), new Exception("File not found: "+fileName))
  }

  /** Casts a string to a url
    *
    * @param uri string to cast from
    * @return converted Uri
    */
  def toUri(uri: String): Either[Exception, URI] = {
    try{
      Right(new URI(uri))
    } catch {
      case e: Exception => Left(new Exception(s"Could not convert to Uri $uri", e))
    }
  }

}
