import Util.dataBuilder.Processing
import Util.database
import Util.file.FileHelper
import Util.UserPrompt._
import cats.data.EitherT
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import Util.ImplicitConversions._
import Util.config.ConfigHandler

object Main extends TaskApp {
   override def run(args: List[String]): Task[ExitCode] = {
     val database = database.MongoDB

     (for {
      config        <- EitherT.fromEither[Task](ConfigHandler.init())
      mongoClient   <- EitherT.fromEither[Task](database.getMongoClient(config))
      db            <- EitherT(database.getDatabase(mongoClient))
      userInput     <- EitherT(promptUser())
      csvFiles      <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      orderedFiles  =  csvFiles.zipWithIndex.toOrderedFile()
      _             <- EitherT(Processing.csvFiles(orderedFiles)(db))
    } yield {
       database.close(mongoClient)
       ExitCode.Success
     }).valueOrF(Task.raiseError)
  }
}
