package com.github.idarlington.flinkProcessor.processors

import java.util.Properties

import cats.effect.IO
import com.github.idarlington.flinkProcessor.config.{ DecoderConfig, ProcessorConfig }
import com.github.idarlington.flinkProcessor.serialization.DWSerializationSchema
import com.github.idarlington.model.DisruptionWrapper
import io.circe.Decoder.Result
import io.circe.Json
import io.circe.parser
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{ StreamExecutionEnvironment, _ }
import org.apache.flink.streaming.connectors.kafka.{
  FlinkKafkaConsumer,
  FlinkKafkaProducer,
  KafkaSerializationSchema
}
import com.github.idarlington.model.circe.DutchDecoders._
import org.apache.flink.streaming.api.datastream.DataStreamSink

import scala.collection.immutable

object Decoder extends Processor[DisruptionWrapper] {

  val decoderConfig: DecoderConfig = processorConfig.decoder

  val scraperTopic: String = decoderConfig.scraperTopic
  val decoderTopic: String = decoderConfig.topic

  properties.setProperty("group.id", decoderConfig.groupId)

  val consumer: FlinkKafkaConsumer[String] =
    new FlinkKafkaConsumer[String](scraperTopic, new SimpleStringSchema(), properties)

  val producer: FlinkKafkaProducer[DisruptionWrapper] = new FlinkKafkaProducer[DisruptionWrapper](
    decoderTopic,
    new DWSerializationSchema(decoderTopic),
    properties,
    FlinkKafkaProducer.Semantic.AT_LEAST_ONCE
  )

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[DisruptionWrapper] = {
    env
      .addSource(consumer)
      .flatMap {
        parser
          .parse(_)
          .map { jsonRecord =>
            decoder(jsonRecord)
          }
          .toOption
      }
      .flatMap(_.iterator)
      .addSink(producer)
  }

  def decoder(json: Json): immutable.Seq[DisruptionWrapper] = {
    {
      json.hcursor.downField("payload").focus match {
        case None => Vector.empty[Json]
        case Some(value) => value.asArray.getOrElse(Vector.empty[Json])
      }
    }.map {
        _.as[DisruptionWrapper]
      }
      .collect {
        case Right(value) => value
      }
  }
}
