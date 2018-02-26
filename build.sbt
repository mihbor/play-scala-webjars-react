name := """play-scala-react"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  guice,
  ehcache,
  ws,
  specs2 % Test,
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "org.webjars" % "react" % "0.14.0",
  "org.webjars" % "marked" % "0.3.2",
  "org.webjars" % "jquery" % "2.1.4",
  "com.typesafe.akka" %% "akka-persistence-typed" % "2.5-SNAPSHOT",
  "com.typesafe.akka" %% "akka-testkit-typed" % "2.5-SNAPSHOT" % Test,
  "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8"

)
dependencyOverrides ++= Set("com.typesafe.akka" %% "akka-actor" % "2.5-SNAPSHOT")


resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
