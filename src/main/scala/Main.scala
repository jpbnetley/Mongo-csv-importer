import Util.DataBuilder.Processing
import Util.Database.Database
import Util.File.FileHelper
import Util.UserPrompt._
import cats.data.EitherT
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import Util.ImplicitConversions._

object Main extends TaskApp {
   override def run(args: List[String]): Task[ExitCode] = {

     implicit val database: Database.type = Database

     (for {
      userInput     <- EitherT(promptUser())
      csvFiles      <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      orderedFiles  =  csvFiles.zipWithIndex.toOrderedFile()
      _             <- EitherT(Processing.csvFiles(orderedFiles))
    } yield {
       database.close()
       ExitCode.Success
     }).valueOrF(Task.raiseError)
  }
}
