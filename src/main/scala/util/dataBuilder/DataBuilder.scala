package util.dataBuilder
import org.bson.Document
import cats.implicits._
import monix.eval.Task
import util.Logging.log
import util.ErrorHandler._

object DataBuilder {

  /** Builds mongo documents form json
    *
    * @param headers   for the csv files
    * @param lineItems that contains the csv data
    * @return bsonDocument as String
    */
  def buildMongoDocuments(headers: Option[List[String]], lineItems: List[String]): Task[Either[Exception, List[Document]]] = Task {
    log.debug("finding headers")
    val header              = Either.fromOption(headers, error("No Headers found for csv"))
    header.map(headers => buildJsonObject(headers, lineItems))
  }

  /** Checks of the line items are the same length as the headers, otherwise adds NULL for the missing headers
    *
    * @param lineRow the single line item that is comma separated (csv)
    * @param maxIndex the max index for the line items
    * @return Csv with null for empty items
    */
  def handleShortColumnHeader(lineRow: String, maxIndex: Int): List[String] = {
    @scala.annotation.tailrec
    def loopOverCsvString(acc: List[String], currentIndex: Int, maxIndex: Int): List[String] = {
      if(currentIndex == maxIndex)
        acc
      else
        loopOverCsvString(acc :+ "NULL", currentIndex + 1, maxIndex)
    }

    val items         = lineRow.split(',').toList
    loopOverCsvString(items, 0, maxIndex)
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
    * @param currentIndex position of the parser
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
    * @param currentIndex value for the parser
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

  /** Builds json objects from the headers and body items
    *
    * @param headerValue list of headers for the object (key)
    * @param lineItems body of the object (value)
    * @return List[Document]
    */
  def buildJsonObject(headerValue: List[String], lineItems: List[String]): List[Document] = {
    log.debug("Parsing json")
    val maxHeaderIndex    = headerValue.length - 1
    val jsonObjects       = lineItems.map { line =>
      val nonEmptyCsvLine = handleShortColumnHeader(line, maxHeaderIndex)

      //Build json object for each line item
      headerValue.zip(nonEmptyCsvLine).zipWithIndex.map { case ((headerText, item), index) =>
        jsonPartialBuilder(index, headerText, item, maxHeaderIndex)
      }.mkString.trim
    }

    jsonObjects.map { row =>
      println(row.mkString.trim)
      Document.parse(row)}
  }
}
