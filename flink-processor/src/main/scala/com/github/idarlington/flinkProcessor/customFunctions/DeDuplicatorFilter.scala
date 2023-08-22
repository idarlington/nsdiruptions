package com.github.idarlington.flinkProcessor.customFunctions

import com.github.idarlington.model.{DisruptionBaseV3, DisruptionWrapperV2}
import org.apache.flink.api.common.functions.RichFilterFunction
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.configuration.Configuration

class DeDuplicatorFilter extends RichFilterFunction[DisruptionBaseV3] {

  @transient
  private var disruptionIds: MapState[String, Boolean] = _

  override def open(parameters: Configuration): Unit = {
    disruptionIds = getRuntimeContext.getMapState[String, Boolean](
      new MapStateDescriptor("disruptionIds", classOf[String], classOf[Boolean])
    )
  }

  override def filter(wrapper: DisruptionBaseV3): Boolean = {
    if (disruptionIds.contains(wrapper.id)) {
      false
    } else {
      disruptionIds.put(wrapper.id, true)
      true
    }
  }
}
