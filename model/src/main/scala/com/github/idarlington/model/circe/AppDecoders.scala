package com.github.idarlington.model.circe

import com.github.idarlington.model.{ Calamity, DisruptionBaseV3, DisruptionType, DisruptionV3 }
import io.circe.Decoder
import io.circe.generic._
import io.circe.generic.auto._
import cats.syntax.functor._

object AppDecoders {

  implicit val V3Decoder: Decoder[DisruptionBaseV3] =
  semiauto.deriveDecoder[DisruptionV3].widen or semiauto
    .deriveDecoder[Calamity]
    .widen

  implicit val disruptionTypeDecoder: Decoder[DisruptionType] = Decoder.instance[DisruptionType] {
    _.as[String].map {
      case "NOTIFICATION_PRIO_1" => DisruptionType.NOTIFICATION_PRIO_1
      case "NOTIFICATION_PRIO_2" => DisruptionType.NOTIFICATION_PRIO_2
      case "NOTIFICATION_PRIO_3" => DisruptionType.NOTIFICATION_PRIO_3
      case "FAULT" => DisruptionType.FAULT
      case "ACTIVITY" => DisruptionType.ACTIVITY
      case "EVENT" => DisruptionType.EVENT
    }
  }
}
