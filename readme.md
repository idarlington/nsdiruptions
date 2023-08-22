# NS-Disruptions

- Scrapes the disruptions from api provided by the Dutch train service (NS)

## Getting Started

This project is built using

- Scala
- FS2
- Flink
- HTTP4S
- Kafka
- Postgres

It contains a couple of microservices

- [Scraper](scraper/.) service that scrapes disruptions from the NS API and dumps in a Kafka topic
- [Decoder](flink-processor/src/main/scala/com/github/idarlington/flinkProcessor/processors/Decoder.scala) service
  that decodes this data and dumps successfully decoded data into Kafka
- [DeDuplicator](flink-processor/src/main/scala/com/github/idarlington/flinkProcessor/processors/DeDuplicator.scala)
  service that filters out duplicate entries and store single entries in a kafka topic
- [DatabaseSink](flink-processor/src/main/scala/com/github/idarlington/flinkProcessor/processors/DatabaseSink.scala)

## Running the project

### Docker Compose

To run the project, a [docker-compose file](docker/docker-compose.yml) is provided.

From the base directory, you can run

```shell
sbt docker:publishLocal && docker-compose -f docker/docker-compose.yml up
```

### Helm

Assuming you already have a Kubernetes cluster setup, you can start one locally
with [minikube](https://minikube.sigs.k8s.io/docs/start/). You can run the project on it.

You can start with exporting your NS API Keys

```shell
export SCRAPER_AUTH_KEY=
```

Run the [helm installation script](scripts/install-helm.sh) for the [provided chart](helm)

## Running the tests

```shell
sbt test 
```
