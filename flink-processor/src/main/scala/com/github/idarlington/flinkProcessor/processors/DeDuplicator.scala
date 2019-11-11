package com.github.idarlington.flinkProcessor.processors

import java.util.Properties

import com.github.idarlington.flinkProcessor.config.ProcessorConfig
import com.github.idarlington.flinkProcessor.customFunctions.DeDuplicatorFilter
import com.github.idarlington.flinkProcessor.serialization.{
  DWDeserializationSchema,
  DWSerializationSchema
}
import com.github.idarlington.model.DisruptionWrapper
import org.apache.flink.streaming.api.scala.{ StreamExecutionEnvironment, _ }
import org.apache.flink.streaming.connectors.kafka.{
  FlinkKafkaConsumer,
  FlinkKafkaProducer,
  KafkaSerializationSchema
}

object DeDuplicator extends App {

  val processorConfig = ProcessorConfig()

  val decodedTopic: String      = processorConfig.decoder.topic
  val deDuplicatorTopic: String = processorConfig.deDuplicator.topic

  val properties = new Properties()

  properties.setProperty("bootstrap.servers", processorConfig.kafka.bootstrapServers)
  properties.setProperty("group.id", processorConfig.deDuplicator.groupId)

  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

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

  env
    .addSource(consumer)
    .map { value =>
      println(value)
      value
    }
    .keyBy(_.id)
    .filter(new DeDuplicatorFilter())
    .addSink(producer)

  env.execute()

}
