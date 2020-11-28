import org.scalatest.funsuite.AnyFunSuite
import util.dataBuilder.DataBuilder._

class JsonParsingTest extends AnyFunSuite {

  val csvData: List[String] = Data.csvData

  test("Build json object") {
    val header: List[String] = Data.header.split(",").toList
    val normal: List[String] = List(Data.normalRow)
    val data: List[String]   = buildJsonObject(header, normal)

    val validation: Boolean = Data.correctJson.exists(data.headOption.contains)

    assert(validation)
  }

}
