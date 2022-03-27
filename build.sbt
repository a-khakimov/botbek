import sbtbuildinfo.BuildInfoKeys

ThisBuild / scalaVersion := "3.1.1"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(
    name := "botbek",
    organization := "org.github.ainr",
    version := "0.1.0-SNAPSHOT",
    assembly / assemblyJarName := "botbek.jar",
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      resolvers,
      BuildInfoKey.action("buildTime") {
        System.currentTimeMillis
      },
      BuildInfoKey.action("gitHeadCommit") {
        git.gitHeadCommit.value map { sha => s"v$sha" }
      }
    ),
    buildInfoPackage := "org.github.ainr.botbek"
  )

libraryDependencies ++= Seq(
  "io.github.apimorphism" %% "telegramium-core" % "7.57.0",
  "io.github.apimorphism" %% "telegramium-high" % "7.57.0",
  "is.cir" %% "ciris" % "2.3.2",
  "lt.dvim.ciris-hocon" %% "ciris-hocon" % "1.0.1",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "org.http4s" %% "http4s-dsl" % "0.23.10",
  "org.http4s" %% "http4s-circe" % "0.23.10",
  "org.http4s" %% "http4s-blaze-client" % "0.23.10",
  "org.typelevel" %% "log4cats-core" % "2.2.0", // Only if you want to Support Any Backend
  "org.typelevel" %% "log4cats-slf4j" % "2.2.0", // Direct Slf4j Support - Recommended
  "io.github.pityka" %% "nspl-awt" % "0.1.0" cross CrossVersion.constant(
    "2.13"
  ),
  "org.slf4j" % "slf4j-api" % "1.7.36"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @_*) => MergeStrategy.discard
  case _                           => MergeStrategy.first
}
