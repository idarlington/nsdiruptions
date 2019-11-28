package com.github.idarlington.model.config

import java.util.Properties

case class KafkaConfig(bootstrapServers: String) {

  def asProperties: Properties = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", bootstrapServers)
    properties
  }
}
