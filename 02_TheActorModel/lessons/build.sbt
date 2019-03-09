name := "week02"

organization := "it.palutz"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.20",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.20",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.20"
)
