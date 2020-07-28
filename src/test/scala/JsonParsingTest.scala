import org.scalatest.funsuite.AnyFunSuite
import util.dataBuilder.DataBuilder._

class JsonParsingTest extends AnyFunSuite {

  val csvData: List[String] = Data.csvData

  test("Build json object") {
    val header  = Data.header.split(",").toList
    val normal  = List(Data.normalRow)
    val data    = buildJsonObject(header, normal)

    val validation = Data.correctJson.exists(data.headOption.contains)

    assert(validation)
  }

}
