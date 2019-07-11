package Util.Models

import java.io.File
import java.net.URI

final case class UserInput(folderPath: URI, skipFiles: List[File] = List.empty[File])
