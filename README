
## Introduction

INI is a tiny process-based language that was designed to build data pipelines in a simple way.
INI was first created and design by Renaud Pawlak as a side research project.
It was extended by Truang Giang Lee during his PhD to introduce better control and synchronization on events, as well as formal verification of INI programs. His PhD was co-supervised by Renaud Rioboo (research director), Renaud Pawlak, Mathieu Manceny, and Olivier Hermant.

## INI Main Features

- Process oriented: programmers can easily define processes that will run on INI nodes and react to events
- Reactive and event-driven: programmers can easily declare events to which processes will react
- Type inference and user types: programmers can define complex structured types and the type checker will enforce the correct usage of the structure
- Rule-based: processes and programs rely on rules for readability purpose
- Functional style: programmers familiar with functional programming can use functions in a minimalist way

## Typical Uses/Applications

- IoT/Robotics
- Distributed Computing

## Language Design Philosophy

Most generic-purpose language make it awfully complex to create data pipelines and synchronize processes and threads to handle the streams and the data. INI has been designed to keep this kinds of programs as simple as they can be. It is not meant to be a general-purpose language that will address all programming issues programmers can face, but to efficiently build data pipelines or calculations that require multiple distributed processes to collaborate around a broker.

## Use with Kafka

In order to use the @Consume event, you need Kafka up and running. For local testing, install and run Kafka as described on the official site.

> cd kafka_2.12-2.2.0
> bin/zookeeper-server-start.sh config/zookeeper.properties
> bin/kafka-server-start.sh config/server.properties

