package util.dataBuilder
import monix.eval.Task
import org.bson.Document
import util.Logging.log

object DataBuilder {

  /** Builds mongo documents form json
    *
    * @param headers   for the csv files
    * @param lineItems that contains the csv data
    * @return bsonDocument as String
    */
  def buildMongoDocuments(headers: List[String], lineItems: List[String]): Task[List[Document]] = Task {
     val jsonObjects = buildJsonObject(headers, lineItems)

    jsonObjects.map { row =>
      log.info("JSON objects: "+row.mkString.trim)
      Document.parse(row)}
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

    val items = lineRow.split(',').toList
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
    val tab = "\t"

    if (currentIndex == 0 && maxIndex == 0) {
      "{ " + headerText + ": \"" + item + "\" }"
    } else if (currentIndex == 0) {
      s"""
         |{
         |$tab$headerText: "$item",""".stripMargin+"\n"
    }
    else if (currentIndex == maxIndex) {
      s"""
         |$tab$headerText: "$item"
         |}
      """.stripMargin
    } else {
      s"""${tab}${headerText}: "${item}","""
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
      "{"
    }
    else if (currentIndex == maxIndex) {
      "}"
    } else {
      ""
    }
  }

  /** Builds json objects from the headers and body items
    *
    * @param headerValue list of headers for the object (key)
    * @param lineItems body of the object (value)
    * @return List[String] in json format
    */
  def buildJsonObject(headerValue: List[String], lineItems: List[String]): List[String] = {
    log.debug("Parsing json")
    val maxHeaderIndex = headerValue.length - 1

    lineItems.map { line =>
      val nonEmptyCsvLine = handleShortColumnHeader(line, maxHeaderIndex)

      //Build json object for each line item
      headerValue.zip(nonEmptyCsvLine).zipWithIndex.map { case ((headerText, item), index) =>
        jsonPartialBuilder(index, headerText, item, maxHeaderIndex)
      }.mkString.trim
    }
  }
}
