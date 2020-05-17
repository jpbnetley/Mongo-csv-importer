package util.databaseProvider

import util.models.SystemConfigPropertiesResponse

trait SystemConfigProperties {
  /** validate of the configuration method is present
    *
    * @return Boolean, true if it exists
    */
  def exists: Boolean

  /** extracts the config variables
    *
    * @return SystemConfigPropertiesResponse
    */
  def extractConfig: Either[Exception, SystemConfigPropertiesResponse]

}
