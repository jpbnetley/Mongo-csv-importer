import java.net.URI

import Util.DataBuilder
import Util.Database.Database
import Util.File.FileHelper
import org.bson.Document

object Main extends App {

  //"/Users/jonathan/Downloads/northwind-mongo-master"
  val userInput = promptUser()
  val pathToCsv = userInput.fold(throw _, identity)
  val csvFiles  = FileHelper.getCsvFiles(pathToCsv)

  for ((file, index) <- csvFiles.zipWithIndex) {
    println(s"Processing file ${index + 1} of ${csvFiles.length}")

    val fileLines       = FileHelper.extractCsvFileLines(file)
    val headers         = fileLines.headOption.map(_.split(',').toList)
    val lineItems       = fileLines.drop(1)
    val collectionName  = file.getName.replace(".csv","").toLowerCase

    val json        = DataBuilder.DataBuilder.buildJsonData(headers, lineItems)
    val result      = json.fold(e => {println(s"ERROR: $e");List.empty[String]}, identity)
    val jsonString  = s"{ [${result.mkString(",")}] }"
    //http://zetcode.com/db/mongodbjava/
//
    val db        = Database.init()
    db.createCollection(collectionName)
    db.getCollection(collectionName).insert
    println(s"Done processing file ${index + 1}")
  }

  def promptUser(): Either[Exception, URI] = {
    try {
//      println("Please enter the path to the csv files: ")
//      val uriInput = scala.io.StdIn.readLine()
//      Right(URI.create(uriInput))
      Right(URI.create("/Users/jonathan/Desktop/test_csv"))
    } catch {
      case e: Exception =>
        val message = s"Could not read user input: ${e.getMessage}"
        Left(new Exception(message))
    }
  }

}
