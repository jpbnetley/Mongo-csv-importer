name := "Mongo-csv-importer"

version := "0.1"

libraryDependencies ++= Seq(
  "org.mongodb.scala"           %% "mongo-scala-driver" % "2.8.0",
  "io.monix"                    %% "monix"              % "3.1.0",
  "org.typelevel"               %% "cats-core"          % "2.1.0",
  "ch.qos.logback"              % "logback-classic"     % "1.2.3",
  "com.typesafe.scala-logging"  %% "scala-logging"      % "3.9.2"
)

scalaVersion := "2.13.1"