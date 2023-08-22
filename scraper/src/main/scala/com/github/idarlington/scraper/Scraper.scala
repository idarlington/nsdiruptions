package com.github.idarlington.scraper

import cats.effect._
import fs2.Pipe
import fs2.kafka.{ produce, ProducerRecord, ProducerRecords, ProducerResult, ProducerSettings }
import io.circe._
import jawnfs2._
import org.http4s
import org.http4s.client.Client
import org.http4s.{ Uri, _ }
import org.typelevel.jawn.Facade

trait Scraper {
  private lazy val producerSettings: ProducerSettings[IO, String, String] =
    ProducerSettings[IO, String, String]
      .withBootstrapServers(scraperConfig.kafka.bootstrapServers)

  def scraperConfig: ScraperConfig

  def scrape(client: Client[IO]): fs2.Stream[IO, Json] = {
    implicit val facade: Facade[Json] = io.circe.jawn.CirceSupportParser.facade
    val nsDisruptionsUri: Uri            = scraperConfig.url

    val request: Request[IO] = Request[IO](
      uri = nsDisruptionsUri,
      headers = http4s.Headers
        .of(http4s.Header("Ocp-Apim-Subscription-Key", scraperConfig.authKey))
    )

    client
      .stream(request)
      .flatMap(_.body.chunks.parseJsonStream)
  }

  def kafkaProduce(
    implicit contextShift: ContextShift[IO]
  ): Pipe[IO, Json, ProducerResult[String, String, Unit]] = { jsonStream =>
    jsonStream
      .map {
        json =>
        ProducerRecords.one { ProducerRecord(scraperConfig.topic, "", json.noSpaces) }
      }
      .through(produce(producerSettings))
  }
}
