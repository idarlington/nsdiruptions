package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionBaseV3
import com.ovoenergy.kafka.serialization.circe.circeJsonDeserializer
import io.circe.generic.auto.*
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.Deserializer

class DW3DeserializationSchema(topic: String) extends KafkaDeserializationSchema[DisruptionBaseV3] {

  private lazy val dwDeserializer: Deserializer[DisruptionBaseV3] =
    circeJsonDeserializer[DisruptionBaseV3]
  override def isEndOfStream(nextElement: DisruptionBaseV3): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): DisruptionBaseV3 = {
    dwDeserializer.deserialize(topic, record.value())
  }

  override def getProducedType: TypeInformation[DisruptionBaseV3] =
    TypeInformation.of(classOf[DisruptionBaseV3])
}
