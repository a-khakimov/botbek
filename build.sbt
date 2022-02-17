ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.github.ainr"
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "botbek",
    assembly / assemblyJarName := "botbek.jar"
  )

libraryDependencies ++= Seq(
  "io.github.apimorphism" %% "telegramium-core" % "7.57.0",
  "io.github.apimorphism" %% "telegramium-high" % "7.57.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "org.http4s" %% "http4s-dsl" % "0.23.10",
  "org.http4s" %% "http4s-circe" % "0.23.10",
  "org.http4s" %% "http4s-blaze-client" % "0.23.10",
  "org.typelevel" %% "log4cats-core" % "2.2.0", // Only if you want to Support Any Backend
  "org.typelevel" %% "log4cats-slf4j" % "2.2.0", // Direct Slf4j Support - Recommended
  "io.github.pityka" %% "nspl-awt" % "0.1.0",
  "org.slf4j" % "slf4j-api" % "1.7.36"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @_*) => MergeStrategy.discard
  case _                           => MergeStrategy.first
}
