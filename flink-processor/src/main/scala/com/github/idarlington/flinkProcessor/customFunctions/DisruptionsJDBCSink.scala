package com.github.idarlington.flinkProcessor.customFunctions

import cats.effect.*
import com.github.idarlington.model.{ StationDisruption, StationDisruptionV3 }
import doobie.*
import doobie.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{ RichSinkFunction, SinkFunction }
import org.postgresql.util.PSQLException

class DisruptionsJDBCSink(dbUrl: String, user: String, password: String)
    extends RichSinkFunction[StationDisruptionV3]
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

  override def invoke(value: StationDisruptionV3, context: SinkFunction.Context[_]): Unit = {

    val query: doobie.Update0 =
      sql"""insert into disruptions
           (disruption_id, station_code, station_uic_code, start_time, end_time, country_code, direction)
           values (
              ${value.disruptionId}, ${value.stationCode}, ${value.stationUicCode}, 
              ${value.start}, ${value.end}, ${value.countryCode}, ${value.direction}
            )
            ON CONFLICT DO NOTHING
            """.update

    query.run.attemptSql
      .transact(transactor)
      .unsafeRunSync()
      .fold(
        error => throw error,
        _ => ()
      )

  }

}
