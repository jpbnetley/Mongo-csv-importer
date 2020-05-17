package util.dataBuilder

import util.dataBuilder.DataBuilder.buildMongoDocuments
import util.file.FileHelper
import cats.data.EitherT
import cats.implicits._
import monix.eval.Task
import org.bson.Document

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import util.ErrorHandler._
import util.models.OrderedFile
import org.mongodb.scala.MongoDatabase

object Processing {

  /** processes csv files, and inserts them into the Database
    *
    * @param csvFiles to process
    * @param database to insert into
    * @return Unit
    */
  def csvFiles(csvFiles: List[OrderedFile])
              (implicit database: MongoDatabase): Task[Either[Exception, Unit]] = {
    val insertion = {
      Task.parTraverse(csvFiles) { orderedFile =>
        println(s"Processing file ${orderedFile.index + 1} of ${csvFiles.length} file name: ${orderedFile.file.name}")
        (for {
          fileLines       <- EitherT(FileHelper.extractCsvFileLines(orderedFile.file))
          headers         <- EitherT.fromEither[Task](getCsvItemsHeader(fileLines))
          lineItems       =  getCsvItemsBody(fileLines)
          collectionName  =  getCollectionNameFromFile(orderedFile.file.name)
          documentResult  <- EitherT.right[Exception](buildMongoDocuments(headers, lineItems))
          dbInsert        <- EitherT.rightT[Task, Exception](database.getCollection[Document](collectionName).insertMany(documentResult))
        } yield {
          println(s"Inserting into db: $dbInsert")
          Await.result(dbInsert.toFuture(), Duration.Inf)
          println(s"Done processing file ${orderedFile.index + 1}")
        }).value
      }
    }

    insertion.map { result =>
      val (errors, _) = result.separate
      errors.headOption.map(error).toLeft(())
    }
  }

  /** Gets the headers names for the csv file.
    *
    * @param fileLines the original line items read form the file.
    * @return List of the csvHeaders
    */
   def getCsvItemsHeader(fileLines: List[String]): Either[Exception, List[String]] = {
    val headers = fileLines.headOption.map(_.split(',').toList)
    Either.fromOption(headers, error("No csv items was found for the header"))
  }

  /** Gets the body of the csv items and removes the headers form
    * the fileLines.
    *
    * @param fileLines the original line items read form the file.
    * @return
    */
   def getCsvItemsBody(fileLines: List[String]): List[String] = {
    fileLines.drop(1)
  }

  /** gets the collection name by removing the .csv extension
    * and changes the name to lowercase.
    *
    * @param fileName the file has. eg users.csv
    * @return Name as String
    */
  def getCollectionNameFromFile(fileName: String): String = {
    fileName.replace(".csv", "").toLowerCase
  }

}
