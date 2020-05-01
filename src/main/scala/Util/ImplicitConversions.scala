package Util

import Util.models.OrderedFile

import scala.reflect.io.File

object ImplicitConversions {
  implicit class listFiles(files : List[(File, Int)]) {
    def toOrderedFile(): List[OrderedFile] = {
      files.map { case (file, index) =>
        OrderedFile(index, file)
      }
    }
  }
}

