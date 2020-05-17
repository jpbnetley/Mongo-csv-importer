package util.config

import util.databaseProvider.SystemConfigProperties
import util.models.SystemConfigPropertiesResponse
import com.typesafe.config.ConfigFactory
import util.ErrorHandler._
import cats.implicits._
import util.Logging.log
import util.ImplicitConversions.RichConfig
import util.models.enums.MongoDbEnvironmentNames

import scala.reflect.io.{Directory, Path}

case object ConfigFile extends SystemConfigProperties {
  /** validate of the configuration method is present
    *
    * @return Boolean, true if it exists
    */
  override def exists: Boolean = {
    val configPath = Path("src/main/resources/application.conf")
    val exist = Directory(configPath).exists
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
        ConfigFactory.load().getString(MongoDbEnvironmentNames.address.value),
        ConfigFactory.load().getInt(MongoDbEnvironmentNames.port.value),
        ConfigFactory.load().getString(MongoDbEnvironmentNames.name.value),
        ConfigFactory.load().getOptionalString(MongoDbEnvironmentNames.username.value),
        ConfigFactory.load().getOptionalString(MongoDbEnvironmentNames.password.value).map(_.toCharArray)
      ).asRight[Exception]

    } catch {
      case e: Exception => errorL(e)
    }

  }

}

