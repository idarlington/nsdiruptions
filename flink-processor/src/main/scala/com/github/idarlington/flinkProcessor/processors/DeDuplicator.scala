package com.github.idarlington.flinkProcessor.processors

import com.github.idarlington.flinkProcessor.config.ProcessorConfig
import com.github.idarlington.flinkProcessor.customFunctions.DeDuplicatorFilter
import com.github.idarlington.flinkProcessor.serialization.{DWDeserializationSchema, DWSerializationSchema}
import com.github.idarlington.model.DisruptionWrapper
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}

object DeDuplicator extends Processor[DisruptionWrapper] {

  val decodedTopic: String      = processorConfig.decoder.topic
  val deDuplicatorTopic: String = processorConfig.deDuplicator.topic

  properties.setProperty("group.id", processorConfig.deDuplicator.groupId)

  val consumer: FlinkKafkaConsumer[DisruptionWrapper] =
    new FlinkKafkaConsumer[DisruptionWrapper](
      decodedTopic,
      new DWDeserializationSchema(decodedTopic),
      properties
    )

  val producer: FlinkKafkaProducer[DisruptionWrapper] = new FlinkKafkaProducer[DisruptionWrapper](
    deDuplicatorTopic,
    new DWSerializationSchema(deDuplicatorTopic),
    properties,
    FlinkKafkaProducer.Semantic.EXACTLY_ONCE
  )

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[DisruptionWrapper] = {
    env
      .addSource(consumer)
      .map { value =>
        value
      }
      .keyBy(_.id)
      .filter(new DeDuplicatorFilter())
      .addSink(producer)

  }
}
