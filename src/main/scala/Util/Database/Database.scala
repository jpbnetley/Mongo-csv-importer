package Util.Database
import com.mongodb.{MongoClientSettings, MongoCredential, ServerAddress}
import com.mongodb.MongoCredential._
import org.mongodb.scala.{MongoClient, MongoDatabase}
import collection.JavaConverters._

object Database {

  /** builds mongo settings with auth
    *
    * @param authUser on the db
    * @param authPassword of the db
    * @param port for the Database
    * @param address to the Database
    * @return MongoClientSettings.Builder
    */
  private def mongoSettingsBuilder(authUser: String, authPassword: Array[Char], port: Int, address: String): MongoClientSettings.Builder = {
    val credential: MongoCredential = createScramSha256Credential(authUser, address, authPassword)
    MongoClientSettings.builder()
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava).build())
      .credential(credential)
  }

  /** Builds mongo settings without auth
    *
    * @param address to the Database
    * @param port for the Database
    * @return MongoClientSettings.Builder
    */
  private def mongoSettingsBuilder(address: String, port: Int): MongoClientSettings.Builder = {
    MongoClientSettings.builder()
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava)build())
  }

  /** Extracts the env variables and returns the mongo settings
    *
    * @return MongoClientSettings.Builder
    */
  private def settingsBuilder(): Option[MongoClientSettings.Builder] = {
    for{
    address  <- sys.env.get("mongo_address")
    port     <- sys.env.get("mongo_port").map(_.toInt)
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

  /** Returns thee database instance
    *
    * @return
    */
  def getDatabase(): MongoDatabase = {
    database
  }

  /** Closes the mongo connection
    *
    */
  def close(): Unit = mongoClient.close()

}
