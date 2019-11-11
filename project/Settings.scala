import sbt._
import Keys._

object Settings {

  lazy val settings = Seq(
    organization := "com.github.idarlington",
    version := "0.0.1",
    scalaVersion := "2.12.10",
    publishMavenStyle := true,
    scalacOptions += "-Ypartial-unification",
    publishArtifact in Test := false
  )

  lazy val testSettings = Seq(
    fork in Test := false,
    parallelExecution in Test := false
  )

  lazy val itSettings = Defaults.itSettings ++ Seq(
    logBuffered in IntegrationTest := false,
    fork in IntegrationTest := true
  )

  lazy val flinkProcessorSettings = Seq(
    fork in run := true
  )

  lazy val modelSettings = Seq()

  lazy val scraperSettings = Seq()

}
