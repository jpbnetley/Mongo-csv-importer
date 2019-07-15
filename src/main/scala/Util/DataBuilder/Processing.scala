package Util.DataBuilder

import Util.DataBuilder.DataBuilder.buildMongoDocuments
import Util.Database.Database
import Util.File.FileHelper
import cats.data.EitherT
import cats.implicits._
import monix.eval.Task
import org.bson.Document

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.io.File

object Processing {

  /** processes csv files, and inserts them into the Database
    *
    * @param csvFiles to process
    * @param database to insert into
    * @return Unit
    */
  def csvFiles(csvFiles: List[(File, Int)])
              (implicit database: Database.type): Task[Either[Exception, Unit]] = {
    Task.wanderUnordered(csvFiles) { case (file, index) =>
      println(s"Processing file ${index + 1} of ${csvFiles.length} file name: ${file.name}")
      (for {
        fileLines       <- EitherT(FileHelper.extractCsvFileLines(file))
        headers         =  fileLines.headOption.map(_.split(',').toList)
        lineItems       =  fileLines.drop(1)
        collectionName  =  file.name.replace(".csv", "").toLowerCase
        mongoDocuments  =  buildMongoDocuments(headers, lineItems)
        documentResult  <- EitherT.fromEither[Task](mongoDocuments)
        db              <- EitherT.right[Exception](database.getDatabase())
        dbInsert        <- EitherT.rightT[Task, Exception](db.getCollection[Document](collectionName).insertMany(documentResult))
      } yield {
        println(s"Inserting into db: $dbInsert")
        Await.result(dbInsert.toFuture(), Duration.Inf)
        println(s"Done processing file ${index + 1}")
      }).value
    }.map { result =>
      val (errors, _) = result.separate
      errors.headOption.toLeft(())
    }
  }

}
