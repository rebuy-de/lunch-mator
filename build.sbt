import scala.language.postfixOps

name := """lunch-mator"""

version := "2.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

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
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
