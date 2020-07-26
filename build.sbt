import Dependencies._

name := "logpack"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

val CirceVersion          = "0.13.0"
val CirceGenericExVersion = "0.13.0"
val CirceConfigVersion    = "0.8.0"
val CirceYamlVersion      = "0.12.0"

libraryDependencies += scalaTest                  % Test
libraryDependencies += "co.fs2"                   %% "fs2-core" % "2.2.1"
libraryDependencies += "co.fs2"                   %% "fs2-io" % "2.2.1"
libraryDependencies += "com.github.alirezameskin" %% "sparser" % "0.0.5"
libraryDependencies += "com.monovore"             %% "decline-effect" % "1.0.0"
libraryDependencies += "eu.bitwalker"             % "UserAgentUtils" % "1.21"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-generic"        % CirceVersion,
  "io.circe" %% "circe-literal"        % CirceVersion,
  "io.circe" %% "circe-generic-extras" % CirceGenericExVersion,
  "io.circe" %% "circe-parser"         % CirceVersion,
  "io.circe" %% "circe-config"         % CirceConfigVersion,
  "io.circe" %% "circe-yaml"           % CirceYamlVersion
)

resolvers += "sparser".at("https://dl.bintray.com/meskin/sparser/")

scalacOptions ++= Seq("-deprecation", "-feature")
