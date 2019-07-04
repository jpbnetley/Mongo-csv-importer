package Util.Database
import com.mongodb.{MongoClientSettings, MongoCredential, ServerAddress}
import com.mongodb.MongoCredential._
import org.mongodb.scala.{MongoClient, MongoDatabase}
import collection.JavaConverters._

object Database {
  private val user          = System.getenv("mongo_auth_uname")
  private val address       = System.getenv("mongo_address")
  private val port          = System.getenv("mongo_port").toInt
  private val password      = System.getenv("mongo_auth_pw").toCharArray
  private val databaseName  = System.getenv("mongo_db_name")

  private val credential: MongoCredential = createScramSha256Credential(user, address, password)

  private val settings: MongoClientSettings.Builder = MongoClientSettings.builder()
    .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava).build())
    .credential(credential)

  private val mongoClient: MongoClient = MongoClient(settings.build())

  private val database: MongoDatabase = mongoClient.getDatabase(databaseName)

  def init(): MongoDatabase = {
    database
  }
}
