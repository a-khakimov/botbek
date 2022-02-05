ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file(".")).settings(name := "botbek")

libraryDependencies += "io.github.apimorphism" %% "telegramium-core" % "7.56.0"
libraryDependencies += "io.github.apimorphism" %% "telegramium-high" % "7.56.0"
