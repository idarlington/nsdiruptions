package com.github.idarlington.flinkProcessor.serialization

import com.github.idarlington.model.DisruptionWrapperV2
import com.ovoenergy.kafka.serialization.circe._
import io.circe.generic.auto._
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.Deserializer

class DWDeserializationSchema(topic: String)
    extends KafkaDeserializationSchema[DisruptionWrapperV2]
    with Serializable {

  private lazy val dwDeserializer: Deserializer[DisruptionWrapperV2] =
    circeJsonDeserializer[DisruptionWrapperV2]

  override def deserialize(
    record: ConsumerRecord[Array[Byte], Array[Byte]]
  ): DisruptionWrapperV2 =
    dwDeserializer.deserialize(topic, record.value())

  override def isEndOfStream(nextElement: DisruptionWrapperV2): Boolean = false

  override def getProducedType: TypeInformation[DisruptionWrapperV2] =
    TypeInformation.of(classOf[DisruptionWrapperV2])
}
