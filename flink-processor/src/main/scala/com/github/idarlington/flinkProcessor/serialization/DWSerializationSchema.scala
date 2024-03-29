package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionWrapperV2
import com.ovoenergy.kafka.serialization.circe._
import io.circe.generic.auto._
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer

import java.lang

class DWSerializationSchema(topic: String) extends KafkaSerializationSchema[DisruptionWrapperV2] {

  lazy val stringSerializer: Serializer[String]          = circeJsonSerializer[String]
  lazy val dwSerializer: Serializer[DisruptionWrapperV2] = circeJsonSerializer[DisruptionWrapperV2]

  override def serialize(
    element: DisruptionWrapperV2,
    timestamp: lang.Long
  ): ProducerRecord[Array[Byte], Array[Byte]] = {

    val keyBytes   = stringSerializer.serialize(topic, element.id)
    val valueBytes = dwSerializer.serialize(topic, element)

    new ProducerRecord[Array[Byte], Array[Byte]](topic, keyBytes, valueBytes)
  }
}
