import Util.DataBuilder.Processing
import Util.Database.Database
import Util.File.FileHelper
import Util.UserPrompt._
import cats.data.EitherT
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}

object Main extends TaskApp {
   override def run(args: List[String]): Task[ExitCode] = {

     implicit val database: Database.type = Database

     (for {
      userInput   <- EitherT(promptUser())
      csvFiles    <- EitherT(FileHelper.getCsvFiles(userInput.folderPath, userInput.skipFiles))
      zippedFiles =  csvFiles.zipWithIndex
      _           <- EitherT(Processing.csvFiles(zippedFiles))
    } yield ExitCode.Success).valueOrF(Task.raiseError)
  }
}
