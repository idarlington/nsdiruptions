package com.github.idarlington.model

sealed trait DisruptionBaseV3 {
  def id: String
}

case class StationDisruptionV3(
  disruptionId: String,
  stationUicCode: String,
  stationCode: Option[String],
  start: String,
  direction: String,
  countryCode: String,
  end: Option[String]
)

// TODO represent dates better
case class DisruptionV3(
  id: String,
  title: String,
  topic: Option[String],
  `type`: String,
  isActive: Boolean,
  registrationTime: Option[String],
  releaseTime: Option[String],
  local: Boolean,
  start: String,
  end: Option[String],
  period: Option[String],
  impact: Option[Impact],
  summaryAdditionalTravelTime: Option[SummaryAdditionalTravelTime],
  publicationSections: Seq[PublicationSections],
  timespans: Seq[Timespans],
  alternativeTransportTimespans: Seq[AlternativeTransportTimespans]
) extends DisruptionBaseV3

case class Calamity(
  id: String,
  title: String,
  topic: Option[String],
  description: Option[String],
  priority: String,
  lastUpdated: Option[String],
  buttons: Buttons,
  url: String,
  `type`: String,
  isActive: Boolean
) extends DisruptionBaseV3

case class AlternativeTransportSummary(
  label: String,
  shortLabel: Option[String]
)

case class AlternativeTransport(
  location: Seq[Location],
  label: String,
  shortLabel: Option[String]
)

case class AlternativeTransportTimespans(
  start: String,
  end: Option[String],
  alternativeTransport: AlternativeTransport
)

case class Buttons(
  position: Seq[String],
  items: Seq[Items]
)

case class Consequence(
  section: Section,
  description: Option[String],
  level: String
)

case class Coordinate(
  lat: Double,
  lng: Double
)

case class Impact(
  value: Int
)

case class Items(
  label: String,
  `type`: String,
  accessibilityLabel: String,
  url: String
)

case class Location(
  station: StationReference,
  description: String
)

case class PublicationSections(
  section: Section,
  consequence: Option[Consequence],
  sectionType: String
)

case class Section(
  stations: Seq[StationReference],
  direction: String
)

case class Situation(
  label: String
)

case class Cause(
  label: String
)

case class StationReference(
  uicCode: String,
  stationCode: Option[String],
  name: String,
  coordinate: Option[Coordinate],
  countryCode: String
)

case class SummaryAdditionalTravelTime(
  label: String,
  shortLabel: Option[String],
  minimumDurationInMinutes: Option[Int],
  maximumDurationInMinutes: Option[Int]
)

case class Timespans(
  start: String,
  end: Option[String],
  period: Option[String],
  situation: Situation,
  cause: Option[Cause],
  additionalTravelTime: Option[SummaryAdditionalTravelTime],
  advices: Seq[String],
  alternativeTransport: Option[AlternativeTransportSummary]
)
