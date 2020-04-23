package Util.DataBuilder

import Util.DataBuilder.DataBuilder.buildMongoDocuments
import Util.File.FileHelper
import cats.data.EitherT
import cats.implicits._
import monix.eval.Task
import org.bson.Document

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import Util.ErrorHandler._
import Util.Models.OrderedFile
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
      Task.wander(csvFiles) { orderedFile =>
        println(s"Processing file ${orderedFile.index + 1} of ${csvFiles.length} file name: ${orderedFile.file.name}")
        (for {
          fileLines       <- EitherT(FileHelper.extractCsvFileLines(orderedFile.file))
          headers         =  fileLines.headOption.map(_.split(',').toList)
          lineItems       =  fileLines.drop(1)
          collectionName  =  orderedFile.file.name.replace(".csv", "").toLowerCase
          documentResult  <- EitherT(buildMongoDocuments(headers, lineItems))
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

}
