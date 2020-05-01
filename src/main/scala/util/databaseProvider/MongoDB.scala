package util.databaseProvider
import com.mongodb.{MongoClientSettings, MongoCredential, ServerAddress}
import monix.eval.Task
import org.mongodb.scala.{MongoClient, MongoDatabase}
import util.ErrorHandler._
import util.models.SystemConfigPropertiesResponse
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
  private def settingsBuilder(props: SystemConfigPropertiesResponse): MongoClientSettings.Builder = {

      (props.mongo_auth_uname, props.mongo_auth_pw) match {
        case (Some(usr), Some(pw)) => mongoSettingsBuilder(usr, pw, props.mongo_port, props.mongo_address)
        case _                     => mongoSettingsBuilder(props.mongo_address, props.mongo_port)
      }
  }

  /** Gets the mongo client
    *
    * @return Either error or MongoClient
    */
  def getMongoClient(props: SystemConfigPropertiesResponse): MongoClient = {
    MongoClient(settingsBuilder(props).build())
    }

  /** Returns thee database instance
    *
    * @return
    */
  def getDatabase(props: SystemConfigPropertiesResponse, mongoClient: MongoClient): Task[MongoDatabase] = Task.eval {
      mongoClient.getDatabase(props.mongo_db_name)
    }

  /** Closes the mongo connection
    *
    */
  def close(mongoClient: MongoClient): Unit = mongoClient.close()

}
