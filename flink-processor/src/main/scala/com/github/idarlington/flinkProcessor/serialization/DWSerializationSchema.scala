package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionWrapper
import com.ovoenergy.kafka.serialization.circe._
import io.circe.generic.auto._
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer

import java.lang

class DWSerializationSchema(topic: String) extends KafkaSerializationSchema[DisruptionWrapper] {

  lazy val stringSerializer: Serializer[String]        = circeJsonSerializer[String]
  lazy val dwSerializer: Serializer[DisruptionWrapper] = circeJsonSerializer[DisruptionWrapper]

  override def serialize(
    element: DisruptionWrapper,
    timestamp: lang.Long
  ): ProducerRecord[Array[Byte], Array[Byte]] = {

    val keyBytes   = stringSerializer.serialize(topic, element.id)
    val valueBytes = dwSerializer.serialize(topic, element)

    new ProducerRecord[Array[Byte], Array[Byte]](topic, keyBytes, valueBytes)
  }
}
