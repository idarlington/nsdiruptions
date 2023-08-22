package com.github.idarlington.scraper

import cats.effect.{ ExitCode, IO, IOApp }
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object ScraperApp extends IOApp with Scraper {
  val scraperConfig: ScraperConfig = ScraperConfig()

  def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource.use { client =>
      val stream = for {
        _ <- Stream.sleep(scraperConfig.scrapeDelay)
        scrapeStream <- scrape(client).through(kafkaProduce)
      } yield scrapeStream

      stream.repeat.compile.drain
        .as(ExitCode.Success)
    }
  }

}
