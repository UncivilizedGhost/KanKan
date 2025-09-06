ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "KanKan"
  )
libraryDependencies += "org.scalafx" % "scalafx_3" % "22.0.0-R33"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "3.0.0"

val circeVersion = "0.14.12"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

scalacOptions ++= Seq("-Xmax-inlines:64") // or higher if needed