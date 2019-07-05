package Util.DataBuilder


object DataBuilder {

  def buildJsonData(headers: Option[List[String]], lineItems: List[String]): Either[String, List[String]]= {
    val header = headers match {
      case Some(h) => Right(h)
      case None    => Left("No Headers found for csv")
    }

    header.map { headerValue =>
        lineItems.flatMap { line =>
          headerValue.zip(line.split(',').toList).zipWithIndex.map { case ((headerText, item), index) =>
            formatJson(index, headerText, item, headerValue.length -1)
          }
        }
    }
  }

  def formatJson(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (currentIndex == 0 && maxIndex == currentIndex) {
      s"{ $headerText : $item }"
    } else if (currentIndex == 0) {
      s"""
         |{
         |   $headerText: $item""".stripMargin
    }
    else if (currentIndex == maxIndex) {
      s"""
         |   $headerText: $item
         |}
      """.stripMargin
    } else {
      s"$headerText: $item"
    }
  }


}
