package com.github.idarlington.flinkProcessor.processors

import java.util.Properties

import com.github.idarlington.flinkProcessor.config.{ DBConfig, ProcessorConfig }
import com.github.idarlington.flinkProcessor.customFunctions.DisruptionsJDBCSink
import com.github.idarlington.flinkProcessor.serialization.DWDeserializationSchema
import com.github.idarlington.model.{ DisruptionWrapper, StationDisruption }
import org.apache.flink.streaming.api.scala.{ StreamExecutionEnvironment, _ }
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

object Remodeller extends App {

  val processorConfig: ProcessorConfig = ProcessorConfig()
  val dbConfig: DBConfig               = processorConfig.db
  val deDuplicatorTopic: String        = processorConfig.deDuplicator.topic

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", processorConfig.kafka.bootstrapServers)
  properties.setProperty("group.id", processorConfig.reModeller.groupId)

  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

  val consumer: FlinkKafkaConsumer[DisruptionWrapper] =
    new FlinkKafkaConsumer[DisruptionWrapper](
      deDuplicatorTopic,
      new DWDeserializationSchema(deDuplicatorTopic),
      properties
    )

  val keyFunction: ((StationDisruption, Int)) => String = {
    case (disruption: StationDisruption, count: Int) =>
      disruption.stationCode
  }

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

  env.execute()

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
