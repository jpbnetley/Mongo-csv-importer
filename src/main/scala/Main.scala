import java.io.File
import java.net.URI

import Util.DataBuilder
import Util.Database.Database
import Util.File.FileHelper
import Util.Models.UserInput
import org.bson.Document

object Main extends App {

  val userInput       = promptUser()
  val userInputObject = userInput.fold(throw _, identity)
  val csvFiles        = FileHelper.getCsvFiles(userInputObject.folderPath, userInputObject.skipFiles).fold(throw _, identity)

  for ((file, index) <- csvFiles.zipWithIndex) {
    println(s"Processing file ${index + 1} of ${csvFiles.length} file name: ${file.getName}")

    val fileLines       = FileHelper.extractCsvFileLines(file)
    val headers         = fileLines.headOption.map(_.split(',').toList)
    val lineItems       = fileLines.drop(1)
    val collectionName  = file.getName.replace(".csv","").toLowerCase

    val mongoDocuments  = DataBuilder.DataBuilder.buildMongoDocuments(headers, lineItems)
    val documentResult  = mongoDocuments.fold(e => {println(s"ERROR: $e");List.empty[Document]}, identity)

    val db        = Database.init()
    db.createCollection(collectionName)
    db.getCollection[Document](collectionName).insertMany(documentResult)
    println(s"Done processing file ${index + 1}")
  }

  def promptUser(): Either[Exception, UserInput] = {
    //"/Users/jonathan/Downloads/northwind-mongo-master"
    //windows box: D:/Temp/test
    try {
//      println("Please enter the path to the csv files: ")
//      val uriInput  = FileHelper.toUri(scala.io.StdIn.readLine()).fold(throw _, identity)
//      val skipItems = addSkipItem(List.empty[String]).flatMap(FileHelper.findFile(uriInput, _))
//      Right(UserInput(uriInput, skipItems))

      //temp hardcoded for testing purposes
      val testUrl = URI.create("/Users/jonathan/Downloads/northwind-mongo-master")
      val fileFilter = FileHelper.findFile(testUrl, "northwind.csv").toList
      Right(UserInput(testUrl, fileFilter))
    } catch {
      case e: Exception =>
        val message = s"Could not read user input: ${e.getMessage}"
        Left(new Exception(message))
    }
  }

  def addSkipItem(inputItems: List[String]): List[String] = {
    do {
      println("Please enter any files to skip (enter :q to exit): ")
      val input = scala.io.StdIn.readLine()
      addSkipItem(inputItems :+ input)
    } while (inputItems.lastOption.contains(":q"))
    inputItems.drop(inputItems.length - 1)
  }

}
