package com.github.idarlington.model.circe

import cats.implicits._
import com.github.idarlington.model._
import io.circe.Decoder
import io.circe.generic.auto._

object DutchDecoders {

  implicit val priceRouteDecoder: Decoder[PriceRoute] = (
    Decoder.instance(_.downField("idVerkoopcombinatie").as[Double]),
    Decoder.instance(_.downField("bedrag").as[Double]),
    Decoder.instance(_.downField("opstaptarief").as[Double])
  ).mapN(PriceRoute)

  implicit val trajectoriesDecoder: Decoder[Trajectories] = (
    Decoder.instance(_.downField("stations").as[List[String]]),
    Decoder.instance(_.downField("begintijd").as[String]),
    Decoder.instance(_.downField("eindtijd").as[String]),
    Decoder.instance(_.downField("richting").as[Option[String]]),
    Decoder.instance(_.downField("vervoerders").as[Option[Double]]),
    Decoder.instance(_.downField("naamVervoer").as[Option[String]]),
    Decoder.instance(_.downField("cdTariefpuntVan").as[Option[Double]]),
    Decoder.instance(_.downField("cdTariefpuntNaar").as[Option[Double]]),
    Decoder.instance(_.downField("afstand1eKlasse").as[Option[Double]]),
    Decoder.instance(_.downField("afstand2eKlasse").as[Option[Double]]),
    Decoder.instance(_.downField("indVolledig2eKlasse").as[Option[Boolean]]),
    Decoder[Option[List[PriceRoute]]].prepare(_.downField("prijsTraject"))
  ).mapN(Trajectories)

  implicit val trackSectionsDecoder: Decoder[TrackSections] =
    Decoder.instance(_.downField("stations").as[List[String]]).map(TrackSections)

  implicit val disruptionTravelAdviceDecoder: Decoder[DisruptionTravelAdvice] = (
    Decoder.instance(_.downField("titel").as[String]),
    Decoder.instance(_.downField("advies").as[List[String]])
  ).mapN(DisruptionTravelAdvice)

  implicit val travelAdviceDecoder: Decoder[TravelAdvice] = (
    Decoder.instance(_.downField("titel").as[String]),
    Decoder[List[DisruptionTravelAdvice]].prepare(_.downField("reisadvies"))
  ).mapN(TravelAdvice)

  implicit val laneRestrictionDecoder: Decoder[LaneRestriction] = (
    Decoder[From].prepare(_.downField("van")),
    Decoder[From].prepare(_.downField("tot")),
    Decoder[List[From]].prepare(_.downField("via")),
    Decoder.instance(_.downField("richting").as[String])
  ).mapN(LaneRestriction)

  implicit val validityListDecoder: Decoder[ValidityList] = (
    Decoder.instance(_.downField("startDatum").as[String]),
    Decoder.instance(_.downField("eindDatum").as[String]),
    Decoder.instance(_.downField("startTijd").as[Option[String]]),
    Decoder.instance(_.downField("eindTijd").as[Option[String]])
  ).mapN(ValidityList)

  implicit val disruptionDecoder: Decoder[Disruption] =
    for {
      id <- Decoder.instance(_.downField("id").as[String])
      laneRestriction <- Decoder[Option[List[LaneRestriction]]]
        .prepare(_.downField("baanvakBeperking"))
      reason <- Decoder.instance(_.downField("reden").as[Option[String]])
      extraTravelTime <- Decoder.instance(_.downField("extraReistijd").as[Option[String]])
      leafletUrl <- Decoder.instance(_.downField("leafletUrl").as[Option[String]])
      travelAdvice <- Decoder[Option[TravelAdvice]].prepare(_.downField("reisadviezen"))
      validityList <- Decoder[List[ValidityList]].prepare(_.downField("geldigheidsLijst"))
      expectation <- Decoder.instance(_.downField("verwachting").as[Option[String]])
      consequence <- Decoder.instance(_.downField("gevolg").as[Option[String]])
      consequenceType <- Decoder.instance(_.downField("gevolgType").as[Option[String]])
      phase <- Decoder.instance(_.downField("fase").as[Option[String]])
      impact <- Decoder.instance(_.downField("impact").as[Option[Double]])
      society <- Decoder.instance(_.downField("maatschappij").as[Option[Double]])
      alternativeTransportation <- Decoder.instance(
        _.downField("alternatiefVervoer").as[Option[String]]
      )
      rural <- Decoder.instance(_.downField("landelijk").as[Boolean])
      cause <- Decoder.instance(_.downField("oorzaak").as[String])
      header <- Decoder.instance(_.downField("header").as[String])
      reportingTime <- Decoder.instance(_.downField("meldtijd").as[Option[String]])
      period <- Decoder.instance(_.downField("periode").as[Option[String]])
      disruptionType <- Decoder.instance {
        _.downField("type").as[String].map {
          case "MELDING_PRIO_1" => DisruptionType.NOTIFICATION_PRIO_1
          case "MELDING_PRIO_2" => DisruptionType.NOTIFICATION_PRIO_2
          case "MELDING_PRIO_3" => DisruptionType.NOTIFICATION_PRIO_3
          case "STORING" => DisruptionType.FAULT
          case "WERKZAAMHEID" => DisruptionType.ACTIVITY
          case "EVENEMENT" => DisruptionType.EVENT
        }
      }
      trackSections <- Decoder[Option[List[TrackSections]]].prepare(_.downField("baanvakken"))
      trajectories <- Decoder[List[Trajectories]].prepare(_.downField("trajecten"))
      version <- Decoder.instance(_.downField("versie").as[Option[String]])
      serialNumber <- Decoder.instance(_.downField("volgnummer").as[Option[String]])
      priority <- Decoder.instance(_.downField("prioriteit").as[Double])
    } yield {
      new Disruption(
        id,
        laneRestriction,
        reason,
        extraTravelTime,
        leafletUrl,
        travelAdvice,
        validityList,
        expectation,
        consequence,
        consequenceType,
        phase,
        impact,
        society,
        alternativeTransportation,
        rural,
        cause,
        header,
        reportingTime,
        period,
        disruptionType,
        trackSections,
        trajectories,
        version,
        serialNumber,
        priority
      )
    }

  implicit val disruptionWrapperDecoder: Decoder[DisruptionWrapper] = (
    Decoder.instance(_.downField("id").as[String]),
    Decoder.instance(_.downField("type").as[String]),
    Decoder.instance(_.downField("titel").as[String]),
    Decoder.instance(_.downField("topic").as[Option[String]]),
    Decoder[Disruption].prepare(_.downField("verstoring"))
  ).mapN(DisruptionWrapper)
}
