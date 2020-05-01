package util.config

import util.models.SystemConfigPropertiesResponse
import util.ErrorHandler._
import util.databaseProvider.SystemConfigProperties

case object ConfigHandler {
  def init: Either[Exception, SystemConfigPropertiesResponse] = {
    val environments: List[SystemConfigProperties] = List(ConfigFile, EnvironmentVariables)

    getEnvironment(environments)
  }

  private def getEnvironment(environments: List[SystemConfigProperties]): Either[Exception, SystemConfigPropertiesResponse]  = {
    environments.find(_.exists).map(_.extractConfig)
      .getOrElse(errorL("No environments are defined"))
  }
}
