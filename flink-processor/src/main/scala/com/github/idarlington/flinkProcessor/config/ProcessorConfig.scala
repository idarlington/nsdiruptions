package com.github.idarlington.flinkProcessor.config

import com.github.idarlington.model.config.KafkaConfig
import pureconfig._
import pureconfig.generic.auto._

case class DecoderConfig(topic: String, scraperTopic: String, groupId: String)

case class DeDuplicatorConfig(topic: String, groupId: String)

case class RemodellerConfig(topic: String, groupId: String)

case class DBConfig(url: String, user: String, password: String)

case class ProcessorConfig(
  kafka: KafkaConfig,
  db: DBConfig,
  decoder: DecoderConfig,
  deDuplicator: DeDuplicatorConfig,
  reModeller: RemodellerConfig
)

object ProcessorConfig {
  def apply(): ProcessorConfig = ConfigSource.default.loadOrThrow[ProcessorConfig]
}
