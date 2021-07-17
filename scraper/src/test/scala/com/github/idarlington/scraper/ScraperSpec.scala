package com.github.idarlington.scraper

import cats.effect._
import fs2.kafka.ProducerSettings
import io.circe._
import io.circe.parser._
import net.manub.embeddedkafka.EmbeddedKafka.withRunningKafka
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.{ BeforeAndAfter, Matchers, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global

class ScraperSpec extends WordSpec with Matchers with BeforeAndAfter {
  private val testScraperConfig     = ScraperConfig()
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  before {
    TestServer.startServer()
  }

  after {
    TestServer.stopServer()
  }

  "Scraper" should {
    "scrape specified url" in {
      val scraper: Scraper = new Scraper {
        override def scraperConfig: ScraperConfig = testScraperConfig
      }

      BlazeClientBuilder[IO](global).resource
        .use { client =>
          scraper
            .scrape(client)
            .map(
              json => {
                json shouldBe parse(TestServer.jsonString).getOrElse(Json.Null)
              }
            )
            .compile
            .drain
        }
        .unsafeRunSync()
    }

    "produce to Kafka" in {
      val scraper: Scraper = new Scraper {
        override def scraperConfig: ScraperConfig = testScraperConfig
      }

      withRunningKafka {
        fs2.Stream
          .eval(IO { parse(TestServer.jsonString).getOrElse(Json.Null) })
          .through(scraper.kafkaProduce)
          .map { result =>
            result.records.size shouldBe (1)
            result.records.foreach {
              case (_, metadata) =>
                metadata.topic() shouldBe (testScraperConfig.topic)
            }
          }
          .compile
          .drain
          .unsafeRunSync()
      }
    }
  }
}
