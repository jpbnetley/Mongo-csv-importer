package util.config

import util.databaseProvider.SystemConfigProperties
import util.models.SystemConfigPropertiesResponse
import com.typesafe.config.{Config, ConfigFactory}
import util.ErrorHandler._
import cats.implicits._
import util.Logging.log
import extensions._

case object ConfigFile extends SystemConfigProperties {
  /** validate of the configeration method is present
    *
    * @return Boolean, true if it exists
    */
  override def exists: Boolean = {
    val exist = !ConfigFactory.load().isEmpty
    log.info(s"Check if config file: application.conf exists: $exist")
    exist
  }

  /** extracts the config variables
    *
    * @return SystemConfigPropertiesResponse
    */
  override def extractConfig: Either[Exception, SystemConfigPropertiesResponse] = {
    try {
      SystemConfigPropertiesResponse(
        ConfigFactory.load().getString("mongo_address"),
        ConfigFactory.load().getInt("mongo_port"),
        ConfigFactory.load().getString("mongo_db_name"),
        ConfigFactory.load().getOptionalString("mongo_auth_uname"),
        ConfigFactory.load().getOptionalString("mongo_auth_pw").map(_.toCharArray)
      ).asRight[Exception]

    } catch {
      case e: Exception => errorL(e)
    }

  }

}

object extensions {

  implicit class RichConfig(val underlying: Config) extends AnyVal {
    def getOptionalString(path: String): Option[String] = if (underlying.hasPath(path)) {
      Some(underlying.getString(path))
    } else {
      None
    }
  }

}
