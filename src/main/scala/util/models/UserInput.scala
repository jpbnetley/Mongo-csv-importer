package util.models

import scala.reflect.io.{Directory, File}

final case class UserInput(folderPath: Directory, skipFiles: List[File] = List.empty[File])
