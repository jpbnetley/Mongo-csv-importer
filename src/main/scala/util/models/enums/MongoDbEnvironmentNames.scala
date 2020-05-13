package util.models.enums
import enumeratum._

sealed abstract class MongoDbEnvironmentName(val value: String) extends EnumEntry
case object MongoDbEnvironmentNames extends Enum[MongoDbEnvironmentName] {
  case object address   extends MongoDbEnvironmentName(value = "mongo_address")
  case object port      extends MongoDbEnvironmentName(value = "mongo_port")
  case object name      extends MongoDbEnvironmentName(value = "mongo_db_name")
  case object username  extends MongoDbEnvironmentName(value = "mongo_auth_uname")
  case object password  extends MongoDbEnvironmentName(value = "mongo_auth_pw")

  val values: IndexedSeq[MongoDbEnvironmentName] = findValues
}

