import org.scalatest.funsuite.AnyFunSuite
import util.dataBuilder.DataBuilder._

class JsonParsingTest extends AnyFunSuite {

  val csvData: List[String] = Data.csvData

  test("Build json object") {
    val header  = Data.header.split(",").toList
    val normal  = List(Data.normalRow)
    val data    = buildJsonObject(header, normal)

    println(data)
    println(Data.correctJson)

    // TODO: this test is failing, even though the data looks identical.
    val validation = Data.correctJson.contains(data.headOption)

    assert(validation)
  }

}
