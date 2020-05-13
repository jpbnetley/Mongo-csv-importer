package util

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Logging {
val log: Logger = Logger(LoggerFactory.getLogger("Mongo csv importer"))
}
