case object Data {
  private val tab="\t"

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

  val header: String      = csvData.headOption.getOrElse("")
  val normalRow: String   = csvData match { case h::m::t => m}
  val shortRow: String    = "Jannie,Fighter"
  val veryShort: String   = "Jannie"

  val correctJson: List[String] = List(
    s"""|{
      |${tab}name: "Jack",
      |${tab}surname: "Johnson",
      |${tab}dob: "1993"
      |}""".stripMargin)
}
