package com.github.idarlington.scraper

import com.github.idarlington.model.config.KafkaConfig
import org.http4s.Uri
import pureconfig._
import pureconfig.error._
import pureconfig.generic.auto._

case class ScraperConfig(url: Uri, authKey: String, topic: String, kafka: KafkaConfig)

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
