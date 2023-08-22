package com.github.idarlington.scraper

import com.github.idarlington.model.config.KafkaConfig
import org.http4s.Uri
import pureconfig.*
import pureconfig.error.*
import pureconfig.generic.auto.*

import scala.concurrent.duration.FiniteDuration

case class ScraperConfig(
  url: Uri,
  authKey: String,
  topic: String,
  scrapeDelay: FiniteDuration,
  kafka: KafkaConfig
)

object ScraperConfig {
  implicit val uriReader: ConfigReader[Uri] = ConfigReader.fromString[Uri] { url: String =>
    Uri
      .fromString(url)
      .left
      .map(parseFailure => CannotConvert(url, Uri.getClass.getCanonicalName, parseFailure.details))
  }

  def apply(): ScraperConfig =
    ConfigSource.default.at("scraper").loadOrThrow[ScraperConfig]

}
