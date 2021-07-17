package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionWrapper
import com.ovoenergy.kafka.serialization.circe._
import io.circe.generic.auto._
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.Deserializer

class DWDeserializationSchema(topic: String)
    extends KafkaDeserializationSchema[DisruptionWrapper]
    with Serializable {

  private lazy val dwDeserializer: Deserializer[DisruptionWrapper] =
    circeJsonDeserializer[DisruptionWrapper]

  override def deserialize(
    record: ConsumerRecord[Array[Byte], Array[Byte]]
  ): DisruptionWrapper =
    dwDeserializer.deserialize(topic, record.value())

  override def isEndOfStream(nextElement: DisruptionWrapper): Boolean = false

  override def getProducedType: TypeInformation[DisruptionWrapper] =
    TypeInformation.of(classOf[DisruptionWrapper])
}
