package util.config

import util.ErrorHandler.error
import util.databaseProvider.SystemConfigProperties
import util.models.SystemConfigPropertiesResponse
import cats.implicits._
import util.Logging.log
import util.models.enums.MongoDbEnvironmentNames

case object EnvironmentVariables extends SystemConfigProperties {
  /** validate of the configuration method is present
    *
    * @return Boolean, true if it exists
    */
  override def exists: Boolean = {
    val exist = extractConfig.isRight
    log.info(s"Check if env vars exists: $exist")
    exist
  }

  /** extracts the config variables
    *
    * @return SystemConfigPropertiesResponse
    */
  override def extractConfig: Either[Exception, SystemConfigPropertiesResponse] = {
    for {
      address       <- Either.fromOption(sys.env.get(MongoDbEnvironmentNames.address.value), error("mongo address not found"))
      port          <- Either.fromOption(sys.env.get(MongoDbEnvironmentNames.port.value).map(_.toInt), error("mongo port not found"))
      databaseName  <- Either.fromOption(sys.env.get(MongoDbEnvironmentNames.name.value), error("environment variables not set for db name"))
    } yield {
      val user          = sys.env.get(MongoDbEnvironmentNames.username.value)
      val password      = sys.env.get(MongoDbEnvironmentNames.password.value).map(_.toCharArray)

      SystemConfigPropertiesResponse(address, port, databaseName, user, password)
    }
  }
}




