name := "Mongo-csv-importer"

version := "0.1"

libraryDependencies ++= {
  val scalaTest: Seq[ModuleID] = {
    val version = "3.2.10"
    Seq(
      "org.scalactic" %% "scalactic"        % version,
      "org.scalatest" % "scalatest_2.13"    % version   % "test"
    )
  }

  Seq(
    "org.mongodb.scala"           %% "mongo-scala-driver"     % "4.7.2",
    "io.monix"                    %% "monix"                  % "3.4.1",
    "org.typelevel"               %% "cats-core"              % "2.8.0",
    "ch.qos.logback"              % "logback-classic"         % "1.4.4",
    "com.typesafe.scala-logging"  %% "scala-logging"          % "3.9.5",
    "com.typesafe"                % "config"                  % "1.4.2",
    "com.beachape"                %% "enumeratum"             % "1.7.0",
  ) ++ scalaTest
}

scalaVersion := "2.13.8"

//http://www.scalatest.org/user_guide/using_scalatest_with_sbt
logBuffered in Test := false
