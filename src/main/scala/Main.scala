import Util.Database.Database
import Util.File.FileHelper

object Main extends App {

  val db        = Database.init()
  val pathToCsv = "/Users/jonathan/Downloads/northwind-mongo-master"
  val csvFiles  = FileHelper.getCsvFiles(pathToCsv)

  for ((file, index) <- csvFiles.zipWithIndex) {
    println(s"Processing file ${index + 1} of ${csvFiles.length}")

    val fileLines = FileHelper.extractCsvFileLines(file)
    val headers   = fileLines.headOption.map(_.split(',').toList)
    val body      = fileLines.drop(1)
    val collectionName = file.getName.replace(".csv","").toLowerCase

    db.createCollection(collectionName)
    db.getCollection(collectionName).bulkWrite(body)//body does not conform to type...
    println(s"Done processing file ${index + 1}")

  }


}
