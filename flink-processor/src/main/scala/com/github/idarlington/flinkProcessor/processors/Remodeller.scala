package com.github.idarlington.flinkProcessor.processors

import java.util.Properties

import com.github.idarlington.flinkProcessor.config.{ DBConfig, ProcessorConfig }
import com.github.idarlington.flinkProcessor.customFunctions.DisruptionsJDBCSink
import com.github.idarlington.flinkProcessor.serialization.DWDeserializationSchema
import com.github.idarlington.model.{ DisruptionWrapper, StationDisruption }
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.{ StreamExecutionEnvironment, _ }
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.flywaydb.core.Flyway

import scala.util.{ Failure, Success, Try }

object Remodeller extends Processor[StationDisruption] {

  val keyFunction: ((StationDisruption, Int)) => String = {
    case (disruption: StationDisruption, count: Int) =>
      disruption.stationCode
  }

  override def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[StationDisruption] = {

    val dbConfig: DBConfig        = processorConfig.db
    val deDuplicatorTopic: String = processorConfig.deDuplicator.topic

    properties.setProperty("group.id", processorConfig.reModeller.groupId)

    val consumer: FlinkKafkaConsumer[DisruptionWrapper] =
      new FlinkKafkaConsumer[DisruptionWrapper](
        deDuplicatorTopic,
        new DWDeserializationSchema(deDuplicatorTopic),
        properties
      )

    env
      .addSource(consumer)
      .flatMap(simplify _)
      .addSink(
        new DisruptionsJDBCSink(
          dbConfig.url,
          dbConfig.user,
          dbConfig.password
        )
      )
  }

  override def main(args: Array[String]): Unit =
    Try {
      val dbConfig = processorConfig.db
      val flyway =
        Flyway.configure().dataSource(dbConfig.url, dbConfig.user, dbConfig.password).load()
      flyway.baseline()
      flyway.migrate()
    }.map { _ =>
      super.main(args)
    }

  def simplify(wrapper: DisruptionWrapper): List[StationDisruption] = {
    wrapper.disruption.trajectories.flatMap { trajectory =>
      trajectory.stations.map { station =>
        StationDisruption(
          stationCode = station,
          startTime   = trajectory.beginTime,
          endTime     = trajectory.endTime,
        )
      }
    }
  }
}
