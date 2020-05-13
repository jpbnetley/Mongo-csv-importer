package util

import com.typesafe.config.Config
import util.models.OrderedFile

import scala.reflect.io.File

object ImplicitConversions {
  implicit class listFiles(files : List[(File, Int)]) {
    def toOrderedFile: List[OrderedFile] = {
      files.map { case (file, index) =>
        OrderedFile(index, file)
      }
    }
  }

  implicit class RichConfig(val underlying: Config) extends AnyVal {
    def getOptionalString(path: String): Option[String] = if (underlying.hasPath(path)) {
      Some(underlying.getString(path))
    } else {
      None
    }
  }
}

