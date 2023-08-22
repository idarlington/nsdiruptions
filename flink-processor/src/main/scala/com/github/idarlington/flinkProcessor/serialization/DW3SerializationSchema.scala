package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionBaseV3
import com.ovoenergy.kafka.serialization.circe.*
import io.circe.generic.auto.*
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer

import java.lang

class DW3SerializationSchema(topic: String) extends KafkaSerializationSchema[DisruptionBaseV3] {

  lazy val stringSerializer: Serializer[String]       = circeJsonSerializer[String]
  lazy val dwSerializer: Serializer[DisruptionBaseV3] = circeJsonSerializer[DisruptionBaseV3]

  override def serialize(
    element: DisruptionBaseV3,
    timestamp: lang.Long
  ): ProducerRecord[Array[Byte], Array[Byte]] = {
    val keyBytes   = stringSerializer.serialize(topic, element.id)
    val valueBytes = dwSerializer.serialize(topic, element)

    new ProducerRecord[Array[Byte], Array[Byte]](topic, keyBytes, valueBytes)

  }
}
