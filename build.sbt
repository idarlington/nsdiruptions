import Dependencies._

import sbt.Keys.version

name := "ns-disruptions"
organization := "com.github.idarlington"
version := "0.0.1"
scalaVersion := Dependencies.version.scala

ThisBuild / resolvers += "Artifactory Realm" at "https://kaluza.jfrog.io/artifactory/maven"

lazy val model = (project in file("model"))
  .settings(libraryDependencies ++= modelDependencies)

lazy val scraper = (project in file("scraper"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(scraperSettings: _*)
  .settings(libraryDependencies ++= scraperDependencies)
  .dependsOn(model)

lazy val `flink-processor` = (project in file("flink-processor"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(flinkProcessorSettings: _*)
  .settings(libraryDependencies ++= flinkProcessorDependencies)
  .dependsOn(model)
  .configs(Test)

// Settings

lazy val commonSettings = Seq(
  publishMavenStyle := true,
  scalacOptions ++= Seq("-Ypartial-unification", "-Xsource:3"),
  Test / publishArtifact := false,
  scalaVersion := Dependencies.version.scala
)

lazy val scraperSettings = commonSettings ++ testSettings

lazy val flinkProcessorSettings = Seq(
  run / fork := true
) ++ commonSettings ++ testSettings

lazy val testSettings = Seq(
  Test / fork := false,
  Test / parallelExecution := false
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  IntegrationTest / logBuffered := false,
  IntegrationTest / fork :=
    true
)
