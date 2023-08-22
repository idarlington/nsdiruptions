package com.github.idarlington.flinkProcessor.processors

import com.github.idarlington.flinkProcessor.config.{ DecoderConfig, ProcessorConfig }
import com.github.idarlington.flinkProcessor.serialization.DW3SerializationSchema
import com.github.idarlington.model.DisruptionBaseV3
import com.github.idarlington.model.circe.AppDecoders.*
import io.circe.{ parser, Json }
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.*
import org.apache.flink.streaming.connectors.kafka.{ FlinkKafkaConsumer, FlinkKafkaProducer }

object Decoder extends Processor[DisruptionBaseV3] {

  private val decoderConfig: DecoderConfig = processorConfig.decoder

  private val scraperTopic: String = decoderConfig.scraperTopic
  private val decoderTopic: String = decoderConfig.topic

  private val consumerProperties = processorConfig.kafka.asProperties
  consumerProperties.setProperty("group.id", decoderConfig.groupId)
  consumerProperties.setProperty("auto.offset.reset", "earliest")

  val consumer: FlinkKafkaConsumer[String] =
    new FlinkKafkaConsumer[String](scraperTopic, new SimpleStringSchema(), consumerProperties)

  val producer: FlinkKafkaProducer[DisruptionBaseV3] =
    new FlinkKafkaProducer[DisruptionBaseV3](
      decoderTopic,
      new DW3SerializationSchema(decoderTopic),
      processorConfig.kafka.asProperties,
      FlinkKafkaProducer.Semantic.AT_LEAST_ONCE
    )

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[DisruptionBaseV3] = {
    env
      .addSource(consumer)
      .flatMap {
        // TODO log error for parsing error
        parser
          .parse(_)
          .map(decoderV3)
          .toOption
      }
      .flatMap(_.iterator)
      .addSink(producer)
  }

  private def decoderV3(record: Json): Seq[DisruptionBaseV3] = {
    record.asArray
      .getOrElse(Vector.empty[Json])
      .map(_.as[DisruptionBaseV3])
      .collect {
        case Right(disruption) => disruption
      }
  }
}
