package com.github.idarlington.flinkProcessor.serialization

import java.lang

import com.github.idarlington.model.DisruptionWrapper
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import com.ovoenergy.kafka.serialization.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import com.github.idarlington.model.circe.AppEncoders._
import org.apache.kafka.common.serialization.Serializer

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
