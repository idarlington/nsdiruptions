package com.github.idarlington.flinkProcessor.processors

import com.github.idarlington.flinkProcessor.config.ProcessorConfig
import com.github.idarlington.flinkProcessor.customFunctions.DeDuplicatorFilter
import com.github.idarlington.flinkProcessor.serialization.{
  DW3DeserializationSchema,
  DW3SerializationSchema,
  DWDeserializationSchema,
  DWSerializationSchema
}
import com.github.idarlington.model.{ DisruptionBaseV3, DisruptionWrapperV2 }
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.*
import org.apache.flink.streaming.connectors.kafka.{ FlinkKafkaConsumer, FlinkKafkaProducer }

import java.util.Properties

object DeDuplicator extends Processor[DisruptionBaseV3] {

  private val decodedTopic: String = processorConfig.decoder.topic
  val deDuplicatorTopic: String    = processorConfig.deDuplicator.topic

  val properties: Properties = processorConfig.kafka.asProperties
  properties.setProperty("group.id", processorConfig.deDuplicator.groupId)
  properties.setProperty("auto.offset.reset", "earliest")

  val consumer: FlinkKafkaConsumer[DisruptionBaseV3] =
    new FlinkKafkaConsumer[DisruptionBaseV3](
      decodedTopic,
      new DW3DeserializationSchema(decodedTopic),
      properties
    )

  val producer: FlinkKafkaProducer[DisruptionBaseV3] =
    new FlinkKafkaProducer[DisruptionBaseV3](
      deDuplicatorTopic,
      new DW3SerializationSchema(deDuplicatorTopic),
      processorConfig.kafka.asProperties,
      FlinkKafkaProducer.Semantic.EXACTLY_ONCE
    )

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[DisruptionBaseV3] = {
    env
      .addSource(consumer)
      .keyBy(_.id)
      .filter(new DeDuplicatorFilter())
      .addSink(producer)
  }
}
