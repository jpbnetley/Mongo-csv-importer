import org.scalatest.funsuite.AnyFunSuite
import util.dataBuilder.DataBuilder._
import util.dataBuilder.Processing

class JsonParsingTest extends AnyFunSuite {

  val csvData = Data.csvData

//  test("Build json object") {
//    val header  = Data.header.split(",").toList
//    val normal  = List(Data.normalRow)
//    val data    = buildJsonObject(header, normal)
//
//    println(data)
//
//    val validation = data.equals(List(
//      """
//        |{
//        |  name: "Jack", surname: "Johnson",
//        |  dob: "1993"
//        |}""".stripMargin))
//    assert(validation)
//  }

}
