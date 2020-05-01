package util.config

import util.databaseProvider.SystemConfigProperties
import util.models.{ConfigFileResponse, SystemConfigPropertiesResponse}
import pureconfig.ConfigSource
import cats.implicits._
import pureconfig.error.ConfigReaderFailures
import util.ErrorHandler._


case object ConfigFile extends SystemConfigProperties {
  /** validate of the configeration method is present
    *
    * @return Boolean, true if it exists
    */
  override def exists: Boolean = {
    ConfigSource.default.load[ConfigFileResponse].leftMap(_.toList.exists(_.location.isEmpty)) match {
      case Right(_) => true
      case Left (e) => e
    }
  }

  /** extracts the config variables
    *
    * @return SystemConfigPropertiesResponse
    */
  override def extractConfig: Either[Exception, SystemConfigPropertiesResponse] = {
   ConfigSource.default.load[ConfigFileResponse]
     .map(toSystemConfigPropertiesResponse)
      .leftMap(toException)
  }

  def toSystemConfigPropertiesResponse(configFileResponse: ConfigFileResponse) = {
    SystemConfigPropertiesResponse(configFileResponse.mongo_address,
      configFileResponse.mongo_port,
      configFileResponse.mongo_db_name,
      configFileResponse.mongo_auth_uname,
      configFileResponse.mongo_auth_pw.map(_.toCharArray))
  }

  def toException(configReaderFailures: ConfigReaderFailures): Exception = {
    configReaderFailures.toList.map(e => error(e.description)).headOption.getOrElse(error("No exception found"))
  }

}
