import Util.DataBuilder
import Util.Database.Database
import Util.File.FileHelper
import Util.UserPrompt._
import org.bson.Document

object Main extends App {
  val userInput       = promptUser()
  val userInputObject = userInput.fold(throw _, identity)
  val csvFiles        = FileHelper.getCsvFiles(userInputObject.folderPath, userInputObject.skipFiles).fold(throw _, identity)
  val database        = Database

  for ((file, index) <- csvFiles.zipWithIndex) {
    println(s"Processing file ${index + 1} of ${csvFiles.length} file name: ${file.getName}")

    val fileLines       = FileHelper.extractCsvFileLines(file)
    val headers         = fileLines.headOption.map(_.split(',').toList)
    val lineItems       = fileLines.drop(1)
    val collectionName  = file.getName.replace(".csv","").toLowerCase

    val mongoDocuments  = DataBuilder.DataBuilder.buildMongoDocuments(headers, lineItems)
    val documentResult  = mongoDocuments.fold(e => {println(s"ERROR: $e");List.empty[Document]}, identity)
    val db       = database.getDatabase()

    for {
      _          <- db.createCollection(collectionName)
      _          <- db.getCollection[Document](collectionName).insertMany(documentResult)
    } yield ()

    println(s"Done processing file ${index + 1}")
  }

  database.close()
}
