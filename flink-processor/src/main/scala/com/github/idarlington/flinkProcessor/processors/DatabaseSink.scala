package com.github.idarlington.flinkProcessor.processors

import com.github.idarlington.flinkProcessor.config.{ DBConfig, ProcessorConfig }
import com.github.idarlington.flinkProcessor.customFunctions.DisruptionsJDBCSink
import com.github.idarlington.flinkProcessor.serialization.{
  DW3DeserializationSchema,
  DWDeserializationSchema
}
import com.github.idarlington.model.*
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.*
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.flywaydb.core.Flyway

import java.util.{ Properties, Random }

object DatabaseSink extends Processor[StationDisruptionV3] {

  val keyFunction: ((StationDisruption, Int)) => String = {
    case (disruption: StationDisruption, count: Int) =>
      disruption.stationCode
  }

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[StationDisruptionV3] = {

    val dbConfig: DBConfig        = processorConfig.db
    val deDuplicatorTopic: String = processorConfig.deDuplicator.topic

    val properties: Properties = processorConfig.kafka.asProperties
    properties.setProperty("group.id", processorConfig.databaseSink.groupId)
    properties.setProperty("auto.offset.reset", "earliest")

    val consumer: FlinkKafkaConsumer[DisruptionBaseV3] =
      new FlinkKafkaConsumer[DisruptionBaseV3](
        deDuplicatorTopic,
        new DW3DeserializationSchema(deDuplicatorTopic),
        properties
      )

    env
      .addSource(consumer)
      .flatMap(remodelV3 _)
      .addSink(
        new DisruptionsJDBCSink(
          dbConfig.url,
          dbConfig.user,
          dbConfig.password
        )
      )
  }

  private def remodelV3(disruption: DisruptionBaseV3): Seq[StationDisruptionV3] = {
    disruption match {
      case disruption: DisruptionV3 =>
        disruption.publicationSections.flatMap { publicationSection =>
          publicationSection.section.stations.map { station =>
            StationDisruptionV3(
              disruptionId   = disruption.id,
              stationUicCode = station.uicCode,
              stationCode    = station.stationCode,
              start          = disruption.start,
              end            = disruption.end,
              direction      = publicationSection.section.direction,
              countryCode    = station.countryCode
            )
          }
        }
      case calamity: Calamity => Seq.empty
    }
  }

  override def main(args: Array[String]): Unit = {
    val dbConfig = processorConfig.db

    val flyway: Flyway = Flyway
      .configure()
      .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
      .baselineOnMigrate(true)
      .load()

    flyway.migrate()
    super.main(args)
  }
}
