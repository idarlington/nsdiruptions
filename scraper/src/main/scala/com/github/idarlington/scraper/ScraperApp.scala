package com.github.idarlington.scraper

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object ScraperApp extends IOApp with Scraper {
  val scraperConfig: ScraperConfig = ScraperConfig()

  def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource.use { client =>
      scrape(client)
        .through(kafkaProduce)
        .compile
        .drain
        .as(ExitCode.Success)
    }
  }

}
