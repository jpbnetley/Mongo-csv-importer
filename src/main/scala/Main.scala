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
     val mongoDb: MongoDB.type = databaseProvider.MongoDB

     (for {
      config        <- EitherT.fromEither[Task](ConfigHandler.init)
      mongoClient   =  mongoDb.getMongoClient(config)
      database      <- EitherT.liftF[Task, Exception, MongoDatabase](mongoDb.getDatabase(config, mongoClient))
      userInput     <- EitherT(promptUser())
      csvFiles      <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      orderedFiles  =  csvFiles.zipWithIndex.toOrderedFile
      _             <- EitherT(Processing.csvFiles(orderedFiles)(database))
    } yield {
       mongoDb.close(mongoClient)
       ExitCode.Success
     }).valueOrF(Task.raiseError)
  }
}
