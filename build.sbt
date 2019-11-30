import Dependencies._

import sbt.Keys.version

name := "ns-disruptions"
organization := "com.github.idarlington"
version := "0.0.1"
scalaVersion := "2.12.10"

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
  scalacOptions += "-Ypartial-unification",
  publishArtifact in Test := false
)

lazy val scraperSettings = commonSettings ++ testSettings

lazy val flinkProcessorSettings = Seq(
  fork in run := true
) ++ commonSettings ++ testSettings

lazy val testSettings = Seq(
  fork in Test := false,
  parallelExecution in Test := false
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  logBuffered in IntegrationTest := false,
  fork in IntegrationTest :=
    true
)
