package Util.Database
import com.mongodb.{MongoClientSettings, MongoCredential, ServerAddress}
import monix.eval.Task
import org.mongodb.scala.{MongoClient, MongoDatabase}
import Util.ErrorHandler._
import cats.implicits._

import scala.jdk.CollectionConverters._

case object MongoDB {

  /** builds mongo settings with auth
    *
    * @param authUser on the db
    * @param authPassword of the db
    * @param port for the Database
    * @param address to the Database
    * @return MongoClientSettings.Builder
    */
  private def mongoSettingsBuilder(authUser: String, authPassword: Array[Char], port: Int, address: String): MongoClientSettings.Builder = {
    val credential: MongoCredential = MongoCredential.createCredential(authUser, "admin", authPassword)
    println("Auth: "+credential.getAuthenticationMechanism.toString)
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
      .applyToClusterSettings(b => b.hosts(List(new ServerAddress(address, port)).asJava).build())
  }

  /** Extracts the env variables and returns the mongo settings
    *
    * @return MongoClientSettings.Builder
    */
  private def settingsBuilder(): Option[MongoClientSettings.Builder] = {
    for {
    address  <- sys.env.get("mongo_address")
    port     <- sys.env.get("mongo_port").map(_.toInt)
    } yield {
      val user          = sys.env.get("mongo_auth_uname")
      val password      = sys.env.get("mongo_auth_pw").map(_.toCharArray)

      (user, password) match {
        case (Some(usr), Some(pw)) => mongoSettingsBuilder(usr, pw, port, address)
        case _                     => mongoSettingsBuilder(address, port)
      }
    }
  }

  /** Gets the mongo client
    *
    * @return Either error or MongoClient
    */
  def getMongoClient: Either[Exception, MongoClient] = {
    Either.fromOption(settingsBuilder(), error("environment variables not set for db")).map { settings =>
      MongoClient(settings.build())
    }
  }

  /** Returns thee database instance
    *
    * @return
    */
  def getDatabase(mongoClient: MongoClient): Task[Either[Exception, MongoDatabase]] = Task.eval {
    Either.fromOption(sys.env.get("mongo_db_name"), error("environment variables not set for db name")).map { databaseName =>
      mongoClient.getDatabase(databaseName)
    }
  }

  /** Closes the mongo connection
    *
    */
  def close(mongoClient: MongoClient): Unit = mongoClient.close()

}