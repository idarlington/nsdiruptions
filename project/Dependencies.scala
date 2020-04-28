import sbt._

object Dependencies {
  lazy val version = new {
    val flyway        = "6.0.1"
    val pureConfig    = "0.12.1"
    val doobie        = "0.8.4"
    val ovoKafkaCirce = "0.5.17"
    val flink         = "1.9.0"
    val scalaTest     = "3.0.0"
    val scalaCheck    = "1.13.4"
    val http4sVersion = "0.21.0-M5"
    val circe         = "0.12.2"
    val fs2Kafka      = "0.20.0"
    val slf4jLog4j    = "1.7.30"
  }

  lazy val library = new {
    val flyway              = "org.flywaydb"          % "flyway-core"                % version.flyway
    val pureConfig          = "com.github.pureconfig" %% "pureconfig"                % version.pureConfig
    val doobieCore          = "org.tpolecat"          %% "doobie-core"               % version.doobie
    val doobiePostgres      = "org.tpolecat"          %% "doobie-postgres"           % version.doobie
    val scalaTest           = "org.scalatest"         %% "scalatest"                 % version.scalaTest % Test
    val scalaCheck          = "org.scalacheck"        %% "scalacheck"                % version.scalaCheck % Test
    val http4sDsl           = "org.http4s"            %% "http4s-dsl"                % version.http4sVersion
    val https4sClient       = "org.http4s"            %% "http4s-blaze-client"       % version.http4sVersion
    val http4sCirce         = "org.http4s"            %% "http4s-circe"              % version.http4sVersion
    val circeGeneric        = "io.circe"              %% "circe-generic"             % version.circe
    val circeLiteral        = "io.circe"              %% "circe-literal"             % version.circe
    val circeParser         = "io.circe"              %% "circe-parser"              % version.circe
    val fs2kafka            = "com.ovoenergy"         %% "fs2-kafka"                 % version.fs2Kafka
    val flinkKafka          = "org.apache.flink"      %% "flink-connector-kafka"     % version.flink
    val flinkScala          = "org.apache.flink"      %% "flink-scala"               % version.flink
    val flinkStreamingScala = "org.apache.flink"      %% "flink-streaming-scala"     % version.flink
    val ovokafkaSerializer  = "com.ovoenergy"         %% "kafka-serialization-core"  % version.ovoKafkaCirce
    val ovoKafkaCirce       = "com.ovoenergy"         %% "kafka-serialization-circe" % version.ovoKafkaCirce
    val slf4jLog4j          = "org.slf4j"             % "slf4j-log4j12"              % version.slf4jLog4j
  }

  val shared: Seq[ModuleID] = Seq(
    library.slf4jLog4j,
    library.pureConfig,
    library.scalaTest,
    library.scalaCheck
  )

  val modelDependencies: Seq[ModuleID] = Seq(
    library.ovoKafkaCirce,
    library.ovokafkaSerializer,
    library.circeParser,
    library.circeGeneric
  ) ++ shared

  val scraperDependencies: Seq[ModuleID] = Seq(
    library.http4sCirce,
    library.https4sClient,
    library.fs2kafka
  )

  val flinkProcessorDependencies: Seq[ModuleID] = Seq(
    library.flyway,
    library.doobieCore,
    library.doobiePostgres,
    library.flinkScala,
    library.flinkKafka,
    library.flinkStreamingScala
  )

}
