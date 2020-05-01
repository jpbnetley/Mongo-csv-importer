package util.config

import util.ErrorHandler.error
import util.databaseProvider.SystemConfigProperties
import util.models.SystemConfigPropertiesResponse
import cats.implicits._

case object EnvironmentVariables extends SystemConfigProperties {
  /** validate of the configeration method is present
    *
    * @return Boolean, true if it exists
    */
  override def exists: Boolean = extractConfig.isRight

  /** extracts the config variables
    *
    * @return SystemConfigPropertiesResponse
    */
  override def extractConfig: Either[Exception, SystemConfigPropertiesResponse] = {
    for {
      address       <- Either.fromOption(sys.env.get("mongo_address"), error("mongo address not found"))
      port          <- Either.fromOption(sys.env.get("mongo_port").map(_.toInt), error("mongo port not found"))
      databaseName  <- Either.fromOption(sys.env.get("mongo_db_name"), error("environment variables not set for db name"))
    } yield {
      val user          = sys.env.get("mongo_auth_uname")
      val password      = sys.env.get("mongo_auth_pw").map(_.toCharArray)

      SystemConfigPropertiesResponse(address, port, databaseName, user, password)
    }
  }
}
