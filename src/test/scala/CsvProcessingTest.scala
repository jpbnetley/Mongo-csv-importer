import org.scalatest.funsuite.AnyFunSuite
import util.dataBuilder.Processing

class CsvProcessingTest extends AnyFunSuite {
/*
setup data to test against.
This will act as the data that is read in from the csv
file.
 */
  val csvData: List[String] = {
    List("name,surname,dob",
      "Jack,Johnson,1993",
      "Jannie,Fighter,1809")
  }

  /** Checks of the function gets the csv headers
    *
    */
  test("Gets csv headers") {
    val data = Processing.getCsvItemsHeader(csvData)
    val validation = data.exists(_.equals(List("name","surname","dob")))
    assert(validation)
  }
  /** Checks of the function gets the csv items after
    * the header was removed.
    *
    */
  test("Gets csv body") {
    val compareResult = {
      List("Jack,Johnson,1993",
        "Jannie,Fighter,1809")
    }
    val data        = Processing.getCsvItemsBody(csvData)
    val validation  = data.diff(compareResult).isEmpty
    assert(validation)
  }
  /**Checks if the function gets the csv file name
    *to be used for the mongodb collection name.
    */
  test("Get collection name from file") {
    val csvFileName = "users.csv"
    val data        = Processing.getCollectionNameFromFile(csvFileName)
    val validation  = data.equals("users")
    assert(validation)
  }

  test("Invoking head on an empty Set should produce NoSuchElementException") {
    assertThrows[NoSuchElementException] {
      Set.empty.head
    }
  }

}
