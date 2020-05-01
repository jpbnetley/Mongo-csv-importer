package Util.config

import Util.models.SystemConfigPropertiesResponse
import Util.ErrorHandler._

case object ConfigHandler {
  def init(): Either[Exception, SystemConfigPropertiesResponse] = {
    val configFile  = ConfigFile
    val envVar      = EnvironmentVariables

    if (configFile.exists){
      configFile.extractConfig
    } else if (envVar.exists) {
      envVar.extractConfig
    } else {
      Left(error("Could not find config"))
    }
  }
}
