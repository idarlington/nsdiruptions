package com.github.idarlington.flinkProcessor.processors

import com.github.idarlington.flinkProcessor.config.ProcessorConfig
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.util.Properties

trait Processor[T] {
  val env: StreamExecutionEnvironment  = StreamExecutionEnvironment.getExecutionEnvironment
  val processorConfig: ProcessorConfig = ProcessorConfig()

  def process(
    env: StreamExecutionEnvironment,
    processorConfig: ProcessorConfig
  ): DataStreamSink[T]

  def main(args: Array[String]): Unit = {
    process(env, processorConfig)
    env.execute()
  }
}
