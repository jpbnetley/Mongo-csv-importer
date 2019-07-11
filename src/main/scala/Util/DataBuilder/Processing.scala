package Util.DataBuilder

import java.io.File

import Util.DataBuilder.DataBuilder.buildMongoDocuments
import Util.Database.Database
import Util.File.FileHelper
import cats.data.EitherT
import cats.implicits._
import monix.eval.Task
import org.bson.Document

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
      println(s"Processing file ${index + 1} of ${csvFiles.length} file name: ${file.getName}")
      (for {
        fileLines       <- EitherT(FileHelper.extractCsvFileLines(file))
        headers         =  fileLines.headOption.map(_.split(',').toList)
        lineItems       =  fileLines.drop(1)
        collectionName  =  file.getName.replace(".csv", "").toLowerCase
        mongoDocuments  =  buildMongoDocuments(headers, lineItems)
        documentResult  <- EitherT.fromEither[Task](mongoDocuments)
        db              <- EitherT.right[Exception](database.getDatabase())
        dbInsert        <- EitherT.rightT[Task, Exception](db.getCollection[Document](collectionName).insertMany(documentResult))
      } yield {
        println(s"Insert into db complete: $dbInsert")
        database.close()
        println(s"Done processing file ${index + 1}")
      }).value
    }.map { result =>
      val (errors, _) = result.separate
      errors.headOption.toLeft(())
    }
  }

}
