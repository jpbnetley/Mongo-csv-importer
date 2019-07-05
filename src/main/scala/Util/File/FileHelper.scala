package Util.File
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets

import scala.io.Source

object FileHelper {

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def getCsvFiles(dir: URI): List[File] = {
    val okFileExtensions = List("csv")
    getListOfFiles(new File(dir.getPath), okFileExtensions)
  }

  def extractCsvFileLines(file: File): List[String] = {
    val reader = Source.fromFile(file.toPath.toUri, StandardCharsets.ISO_8859_1.name())
    reader.getLines().toList
  }

}
