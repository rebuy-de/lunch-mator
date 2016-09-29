import com.arpnetworking.sbt.typescript.Import.TypescriptKeys

name := """lunch-mator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.6"

TypescriptKeys.configFile := "tsconfig.json"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc4",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.webjars.npm" % "angular__common" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__compiler" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__core" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__http" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__platform-browser" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__platform-browser-dynamic" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__upgrade" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__forms" % "2.0.0-rc.6",
  "org.webjars.npm" % "angular__router" % "3.0.0-rc.2",
  "org.webjars.npm" % "core-js" % "2.4.1",
  "org.webjars.npm" % "reflect-metadata" % "0.1.3",
  "org.webjars.npm" % "rxjs" % "5.0.0-beta.11",
  "org.webjars.npm" % "systemjs" % "0.19.27",
  "org.webjars.npm" % "zone.js" % "0.6.17",
  "org.webjars.npm" % "angular2-in-memory-web-api" % "0.0.18",
  "org.webjars.npm" % "bootstrap-sass" % "3.3.6"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
