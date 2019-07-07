package Util.Database
import com.mongodb.{MongoClientSettings, MongoCredential, ServerAddress}
import com.mongodb.MongoCredential._
import org.mongodb.scala.{MongoClient, MongoDatabase}
import collection.JavaConverters._

object Database {

  private def mongoSettingsBuilder(user: String, password: Array[Char], port: Int, address: String): MongoClientSettings.Builder = {
    val credential: MongoCredential = createScramSha256Credential(user, address, password)
    MongoClientSettings.builder()
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava).build())
      .credential(credential)
  }

  private def mongoSettingsBuilder(address: String, port: Int): MongoClientSettings.Builder = {
    MongoClientSettings.builder()
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava).build())
  }

  private def settingsBuilder(): Option[MongoClientSettings.Builder] = {
    for{
    address       <- sys.env.get("mongo_address")
    port          <- sys.env.get("mongo_port").map(_.toInt)
    } yield {
      val user          = sys.env.get("mongo_auth_uname")
      val password      = sys.env.get("mongo_auth_pw").map(_.toCharArray)
      (user, password) match {
        case (Some(usr), Some(pw)) => mongoSettingsBuilder(usr, pw, port, address)
        case _                      =>  mongoSettingsBuilder(address, port)
      }
    }
  }

  private val databaseName              = sys.env.get("mongo_db_name").fold(throw new Exception("env variable not set for mongo_db_name"))(identity)
  private val settings                  = settingsBuilder().fold(throw new Exception("environment variables not set for db"))(identity)
  private val mongoClient: MongoClient  = MongoClient(settings.build())
  private val database: MongoDatabase   = mongoClient.getDatabase(databaseName)

  def init(): MongoDatabase = {
    database
  }
}
