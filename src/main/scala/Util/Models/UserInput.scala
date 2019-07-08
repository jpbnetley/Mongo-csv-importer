package Util.Models

import java.net.URI
import java.io.File

final case class UserInput(folderPath: URI, skipFiles: List[File] = List.empty[File])
