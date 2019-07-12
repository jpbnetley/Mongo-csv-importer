package Util.DataBuilder
import org.bson.Document


object DataBuilder {

  /** Builds mongo documents form json
    *
    * @param headers   for the csv files
    * @param lineItems that contains the csv data
    * @return bsonDocument as String
    */
  def buildMongoDocuments(headers: Option[List[String]], lineItems: List[String]): Either[Exception, List[Document]] = {
    val header = headers match {
      case Some(h) => Right(h)
      case None => Left(new Exception("No Headers found for csv"))
    }

    header.map { headerValue =>
      lineItems.map { line =>
        headerValue.zip(line.split(',').toList).zipWithIndex.map { case ((headerText, item), index) =>
          jsonPartialBuilder(index, headerText, item, headerValue.length - 1)
        }
      }.map{row => println{row.mkString.trim};Document.parse(row.mkString.trim)}
    }
  }

  /** Extracts json per line item
    *
    * @param currentIndex the curser is at
    * @param headerText   for each object
    * @param item         value for the header text
    * @param maxIndex     max length of the csv file
    * @return json object partial string
    */
  def jsonPartialBuilder(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (item != "NULL") {
      parseJsonHandler(currentIndex, headerText, item, maxIndex)
    } else {
      parseJsonNullHandler(currentIndex, headerText, item, maxIndex)
    }
  }

  /** Parses json with no Nulls
    *
    * @param currentIndex
    * @param headerText for each object
    * @param item       value for the header text
    * @param maxIndex   max length of the csv file
    * @return json object partial string
    */
  def parseJsonHandler(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (currentIndex == 0 && maxIndex == 0) {
      "{ " + headerText + ": \"" + item + "\" }"
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
      headerText + ": \"" + item + "\", "
    }
  }

  /** Parses json with that contains a Null
    *
    * @param currentIndex
    * @param headerText for each object
    * @param item       value for the header text
    * @param maxIndex   max length of the csv file
    * @return json object partial string
    */
  def parseJsonNullHandler(currentIndex: Int, headerText: String, item: String, maxIndex: Int): String = {
    if (currentIndex == 0 && maxIndex == 0) {
      "{ }"
    } else if (currentIndex == 0) {
      s"""{"""
    }
    else if (currentIndex == maxIndex) {
      s"""}"""
    } else {
      ""
    }
  }
}
