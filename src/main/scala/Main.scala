import util.dataBuilder.Processing
import util.databaseProvider
import util.file.FileHelper
import util.UserPrompt._
import cats.data.EitherT
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import org.mongodb.scala.MongoDatabase
import util.ImplicitConversions._
import util.config.ConfigHandler
import util.databaseProvider.MongoDB

object Main extends TaskApp {
   override def run(args: List[String]): Task[ExitCode] = {
     val database: MongoDB.type = databaseProvider.MongoDB

     (for {
      config        <- EitherT.fromEither[Task](ConfigHandler.init)
      mongoClient   =  database.getMongoClient(config)
      db            <- EitherT.liftF[Task, Exception, MongoDatabase](database.getDatabase(config, mongoClient))
      userInput     <- EitherT(promptUser())
      csvFiles      <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      orderedFiles  =  csvFiles.zipWithIndex.toOrderedFile
      _             <- EitherT(Processing.csvFiles(orderedFiles)(db))
    } yield {
       database.close(mongoClient)
       ExitCode.Success
     }).valueOrF(Task.raiseError)
  }
}
