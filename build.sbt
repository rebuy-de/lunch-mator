import sbt.internal.util.ManagedLogger
import scala.language.postfixOps

name := "lunch-mator"

scalaVersion := "2.12.4"

version := "2.0"

logLevel in uiProdBuild := Level.Info

lazy val run_ui = inputKey[Unit]("build and watch ui")

lazy val lunch_mator = (project in file(".")).enablePlugins(PlayScala)
  .settings(
    run_ui := {
      javaOptions in run_ui += "-Dconfig.resource=application.test.conf"
      (uiWatch in Compile).evaluated
      (run in Compile).evaluated
    })

libraryDependencies ++= Seq(
  cacheApi,
  ehcache,
  guice,
  ws,
  specs2 % Test,
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc4",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
  "com.typesafe.play" %% "play-ws" % "2.4.3",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.scalaz" %% "scalaz-core" % "7.2.25",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
  "joda-time" % "joda-time" % "2.10",
  "org.joda" % "joda-convert" % "1.7"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

lazy val uiProdBuild = TaskKey[Unit]("uiProdBuild")
lazy val uiWatch = inputKey[Unit]("uiWatchBuild")

uiProdBuild := {
  implicit val uiRoot: File = baseDirectory.value / "ui"
  implicit val log: ManagedLogger = streams.value.log
  if (!UIBuild.buildUiProd) {
    throw new Exception(s"Oops! UI Build crashed")
  }
}

uiWatch := {
  implicit val uiRoot: File = baseDirectory.value / "ui"
  implicit val log: ManagedLogger = streams.value.log
  if (!UIBuild.buildUiWatch) {
    throw new Exception(s"Oops! UI Build crashed")
  }
}

dist := (dist dependsOn uiProdBuild).value
