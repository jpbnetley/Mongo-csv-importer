name := "Mongo-csv-importer"

version := "0.1"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
  "io.monix"          %% "monix"              % "3.0.0-RC3",
  "org.typelevel"     %% "cats-core"          % "2.0.0-M1"
)

scalaVersion := "2.12.8"