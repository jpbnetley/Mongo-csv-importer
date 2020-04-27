import Util.DataBuilder.Processing
import Util.Database
import Util.File.FileHelper
import Util.UserPrompt._
import cats.data.EitherT
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import Util.ImplicitConversions._

object Main extends TaskApp {
   override def run(args: List[String]): Task[ExitCode] = {
     val database: Database.MongoDB.type = Database.MongoDB

     (for {
      userInput     <- EitherT(promptUser())
      csvFiles      <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      orderedFiles  =  csvFiles.zipWithIndex.toOrderedFile()
      mongoClient   <- EitherT.fromEither[Task](database.getMongoClient)
      db            <- EitherT(database.getDatabase(mongoClient))
      _             <- EitherT(Processing.csvFiles(orderedFiles)(db))
    } yield {
       database.close(mongoClient)
       ExitCode.Success
     }).valueOrF(Task.raiseError)
  }
}
