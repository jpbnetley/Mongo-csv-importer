package Util.File
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets

import scala.io.Source

object FileHelper {
  /** Gets a list of files in a directory
    *
    * @param dir to get the fies from
    * @param extensions to only show
    * @return List[Files]
    */
  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
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
  def getCsvFiles(dir: URI, skipFiles: List[File] = List.empty[File]): Either[Exception, List[File]] = {
    val okFileExtensions = List("csv")
    try {
      Right(getListOfFiles(new File(dir.getPath), okFileExtensions).diff(skipFiles))
    } catch {
      case e: Exception =>
        val message = s"Could not read files from path: ${e.getMessage}"
        Left(new Exception(message))
    }
  }

  /** Extracts the csv file content
    *
    * @param file to extract from
    * @return line items
    */
  def extractCsvFileLines(file: File): List[String] = {
    val reader = Source.fromFile(file.toPath.toUri, StandardCharsets.ISO_8859_1.name())
    reader.getLines().toList
  }

  /** finds a file in a directory
    *
    * @param dir to the file
    * @param fileName to find by
    * @return optional found file
    */
  def findFile(dir: URI, fileName: String): Option[File] = {
    new File(dir.getPath).listFiles(_.isFile).find(_.getName == fileName)
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
