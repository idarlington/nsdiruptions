package com.github.idarlington.flinkProcessor.customFunctions

import cats.effect._
import com.github.idarlington.model.StationDisruption
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class DisruptionsJDBCSink(dbUrl: String, user: String, password: String)
    extends RichSinkFunction[StationDisruption]
    with Serializable {

  @transient
  private var transactor: Aux[IO, Unit] = _

  override def open(parameters: Configuration): Unit = {
    @transient
    val executionContext = ExecutionContexts.synchronous

    @transient
    implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)

    transactor = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      dbUrl,
      user,
      password,
      Blocker.liftExecutionContext(executionContext) // TODO Update
    )
  }

  override def invoke(value: StationDisruption, context: SinkFunction.Context[_]): Unit = {

    val query: doobie.Update0 =
      sql"insert into disruption_count (station_code, start_time, end_time) values (${value.stationCode}, ${value.startTime}, ${value.endTime})".update

    query.run
      .attemptSomeSqlState { case _ => }
      .transact(transactor)
      .unsafeRunSync

  }

}
