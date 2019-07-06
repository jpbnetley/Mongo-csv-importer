import java.net.URI

import Util.DataBuilder
import Util.Database.Database
import Util.File.FileHelper
import org.bson.Document

object Main extends App {

  val userInput = promptUser()
  val pathToCsv = userInput.fold(throw _, identity)
  val csvFiles  = FileHelper.getCsvFiles(pathToCsv).fold(throw _, identity)

  for ((file, index) <- csvFiles.zipWithIndex) {
    println(s"Processing file ${index + 1} of ${csvFiles.length}")

    val fileLines       = FileHelper.extractCsvFileLines(file)
    val headers         = fileLines.headOption.map(_.split(',').toList)
    val lineItems       = fileLines.drop(1)
    val collectionName  = file.getName.replace(".csv","").toLowerCase

    val mongoDocuments        = DataBuilder.DataBuilder.buildMongoDocuments(headers, lineItems)
    val documentResult        = mongoDocuments.fold(e => {println(s"ERROR: $e");List.empty[Document]}, identity)

    //http://zetcode.com/db/mongodbjava/
//
//    val db        = Database.init()
//    db.createCollection(collectionName)
//    db.getCollection(collectionName)
    println(s"Done processing file ${index + 1}")
  }

  def promptUser(): Either[Exception, URI] = {
    //"/Users/jonathan/Downloads/northwind-mongo-master"
    //windows box: D:\_JPBN Backup\Downloads\Documents\northwind-mongo-master\test
    try {
//      println("Please enter the path to the csv files: ")
//      val uriInput = scala.io.StdIn.readLine()
//      Right(URI.create(uriInput))
      Right(URI.create("D:/Temp/test"))
    } catch {
      case e: Exception =>
        val message = s"Could not read user input: ${e.getMessage}"
        Left(new Exception(message))
    }
  }

}
