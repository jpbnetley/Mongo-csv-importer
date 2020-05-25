case object Data {
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

  val header    = "name,surname,dob"
  val normalRow = "Jack,Johnson,1993"
  val shortRow  = "Jannie,Fighter"
  val veryShort = "Jannie"
}
