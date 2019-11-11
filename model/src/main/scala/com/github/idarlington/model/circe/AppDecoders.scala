package com.github.idarlington.model.circe

import com.github.idarlington.model.DisruptionType
import io.circe.Decoder

object AppDecoders {

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
