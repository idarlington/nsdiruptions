package com.github.idarlington.model.circe

import com.github.idarlington.model.{ DisruptionType, DisruptionWrapper }
import com.ovoenergy.kafka.serialization.circe._
import io.circe.Encoder
import org.apache.kafka.common.serialization.Serializer

import io.circe.generic.auto._
import io.circe.syntax._

object AppEncoders {

  implicit val encodeDisruptionType: Encoder[DisruptionType] = Encoder.instance {
    _.toString.asJson
  }

}
