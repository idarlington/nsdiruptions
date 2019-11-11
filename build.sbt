import Dependencies._
import Settings._

lazy val model = (project in file("model"))
  .settings(Settings.settings: _*)
  .settings(Settings.modelSettings: _*)
  .settings(libraryDependencies ++= modelDependencies)

lazy val scraper = (project in file("scraper"))
  .settings(Settings.settings: _*)
  .settings(Settings.scraperSettings: _*)
  .settings(libraryDependencies ++= scraperDependencies)
  .aggregate(model)
  .dependsOn(model)

lazy val `flink-processor` = (project in file("flink-processor"))
  .settings(Settings.settings: _*)
  .settings(Settings.flinkProcessorSettings: _*)
  .settings(libraryDependencies ++= flinkProcessorDependencies)
  .dependsOn(model)
  .aggregate(model)
  .configs(Test)
