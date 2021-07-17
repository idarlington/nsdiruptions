package com.github.idarlington.scraper

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.kafka._
import io.circe._
import jawnfs2._
import org.http4s
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Uri, _}
import org.typelevel.jawn.RawFacade

import scala.concurrent.ExecutionContext.Implicits.global

object Scraper extends IOApp with Http4sClientDsl[IO] {

  implicit val facade: RawFacade[Json] = io.circe.jawn.CirceSupportParser.facade

  val scraperConfig: ScraperConfig = ScraperConfig()

  val producerSettings: ProducerSettings[IO, String, String] =
    ProducerSettings[IO, String, String]
      .withBootstrapServers(scraperConfig.kafka.bootstrapServers)

  def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource.use { client =>
      val nsDisruptionsUri: Uri = scraperConfig.url

      val request: Request[IO] = Request[IO](
        uri = nsDisruptionsUri,
        headers = http4s.Headers
          .of(http4s.Header("Ocp-Apim-Subscription-Key", scraperConfig.authKey))
      )

      val jsonStream: fs2.Stream[IO, Json] =
        client.stream(request).flatMap(_.body.chunks.parseJsonStream)

      jsonStream
        .map { json =>
          ProducerRecords.one { ProducerRecord(scraperConfig.topic, "", json.noSpaces) }
        }
        .through(produce(producerSettings))
        .compile
        .drain
        .as(ExitCode.Success)
    }
  }

}
