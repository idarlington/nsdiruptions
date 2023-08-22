package com.github.idarlington.model

trait Direction

object Direction {
  case object OUT extends Direction
  case object BACK_AND_FORTH extends Direction
}

sealed trait DisruptionType

object DisruptionType {
  case object NOTIFICATION_PRIO_1 extends DisruptionType
  case object NOTIFICATION_PRIO_2 extends DisruptionType
  case object NOTIFICATION_PRIO_3 extends DisruptionType
  case object FAULT extends DisruptionType
  case object ACTIVITY extends DisruptionType
  case object EVENT extends DisruptionType
}

case class From(
  code: String,
  empty: Boolean
)

case class LaneRestriction(
  from: From,
  to: From,
  via: List[From],
  direction: String
)

case class DisruptionTravelAdvice(
  title: String,
  advice: List[String]
)

case class TravelAdvice(
  title: String,
  disruptionTravelAdvice: List[DisruptionTravelAdvice]
)

case class ValidityList(
  startDate: String,
  endDate: String,
  startTime: Option[String],
  endTime: Option[String]
)

case class TrackSections(
  stations: List[String]
)

case class PriceRoute(
  idSaleCombination: Double,
  amount: Double,
  boardingRate: Double
)

case class Trajectories(
  stations: List[String],
  beginTime: String,
  endTime: String,
  direction: Option[String],
  carriers: Option[Double],
  TransportName: Option[String],
  cdFarePointFrom: Option[Double],
  cdTariffPointTo: Option[Double],
  Distance1stClass: Option[Double],
  Distance2ndClass: Option[Double],
  indFull2ndClass: Option[Boolean],
  priceRoute: Option[List[PriceRoute]]
)

case class Disruption(
  id: String,
  laneRestriction: Option[List[LaneRestriction]],
  reason: Option[String],
  extraTravelTime: Option[String],
  leafletUrl: Option[String],
  travelAdvice: Option[TravelAdvice],
  validityList: List[ValidityList],
  expectation: Option[String],
  consequence: Option[String],
  consequenceType: Option[String],
  phase: Option[String],
  impact: Option[Double],
  society: Option[Double],
  alternativeTransportation: Option[String],
  rural: Boolean,
  cause: String,
  header: String,
  reportingTime: Option[String],
  period: Option[String],
  `type`: DisruptionType,
  trackSections: Option[List[TrackSections]],
  trajectories: List[Trajectories],
  version: Option[String],
  serialNumber: Option[String],
  priority: Double
)

case class DisruptionWrapperV2(
  id: String,
  `type`: String,
  title: String,
  topic: Option[String],
  disruption: Disruption
)

case class StationDisruption(stationCode: String, startTime: String, endTime: String)
