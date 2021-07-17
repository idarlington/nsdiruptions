package com.github.idarlington.model.circe

import com.github.idarlington.model.DisruptionType
import io.circe.Encoder
import io.circe.syntax._

object AppEncoders {

  implicit val encodeDisruptionType: Encoder[DisruptionType] = Encoder.instance {
    _.toString.asJson
  }

}
