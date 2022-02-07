ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file(".")).settings(name := "botbek")

libraryDependencies += "io.github.apimorphism" %% "telegramium-core" % "7.56.0"
libraryDependencies += "io.github.apimorphism" %% "telegramium-high" % "7.56.0"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.1"
libraryDependencies += "io.circe" %% "circe-core" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.23.10"
libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.10"
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % "0.23.10"
