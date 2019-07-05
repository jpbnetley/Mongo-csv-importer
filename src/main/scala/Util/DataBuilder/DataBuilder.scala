package Util.DataBuilder


object DataBuilder {

  def buildJsonData(headers: Option[List[String]], lineItems: List[String]): Either[String, String] = {
    val header = headers match {
      case Some(h) => Right(h)
      case None    => Left("No Headers found for csv")
    }

    header.map{ headerValue =>
      val file = {
        lineItems.flatMap { line =>
          for {
            headItem  <- headerValue
            csv       <- line.split(',').toList
          } yield s"$headItem : $csv"
        }
      }

      s"""
         |{
         |${file.mkString(", ")}
         |}
       """.stripMargin
    }
  }


}
