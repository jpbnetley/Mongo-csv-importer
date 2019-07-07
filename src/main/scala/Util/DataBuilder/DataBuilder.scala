package Util.DataBuilder
import org.bson.Document

object DataBuilder {

  def buildMongoDocuments(headers: Option[List[String]], lineItems: List[String]): Either[String, List[Document]] = {
    val header = headers match {
      case Some(h) => Right(h)
      case None    => Left("No Headers found for csv")
    }

  header.map { headerValue =>
      lineItems.map { line =>
        headerValue.zip(line.split(',').toList).zipWithIndex.map { case ((headerText, item), index) =>
          jsonPartialBuilder(index, headerText, item, headerValue.length -1)
        }
      }.map(row => convertJsonToDoc(row.mkString))
    }
  }

  def jsonPartialBuilder(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if(item != "NULL"){
      parseJsonHandler(currentIndex, headerText, item, maxIndex)
    } else{
      parseJsonNullHandler(currentIndex, headerText, item, maxIndex)
    }

  }

  def parseJsonHandler(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (currentIndex == 0 && maxIndex == 0) {
      "{ "+headerText+": \""+item+"\" }"
    } else if (currentIndex == 0) {
      s"""
         |{
         |   $headerText: "$item", """.stripMargin
    }
    else if (currentIndex == maxIndex) {
      s"""
         |   $headerText: "$item"
         |}
      """.stripMargin
    } else {
      headerText+": \""+item+"\", "
    }
  }

  def parseJsonNullHandler(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (currentIndex == 0 && maxIndex == 0) {
      "{ }"
    } else if (currentIndex == 0) {
      s"""
         |{""".stripMargin
    }
    else if (currentIndex == maxIndex) {
      s"""
         |}
      """.stripMargin
    } else {
     ""
    }
  }

  def convertJsonToDoc(data: String): Document = {
    import org.bson.Document
    Document.parse(data)
  }


}
