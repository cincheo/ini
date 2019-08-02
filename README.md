
## Context

INI is a tiny process-based language that was designed to build distributed applications in a simple way.
INI was first created and design by Renaud Pawlak as a side research project.
It was extended by Truang Giang Lee during his PhD to introduce better control and synchronization on events, as well as formal verification of INI programs. His PhD was co-supervised by Renaud Rioboo (research director), Renaud Pawlak, Mathieu Manceny, and Olivier Hermant.

## About INI

### Language Design Philosophy

INI has been designed to keep distributed computing as simple as it can be. It natively handles processes, deployment, communication and synchronization, so that the programmers do not have to care about these issues by writing complex code.

INI is not meant to be a general-purpose language that will address all programming issues programmers can face, but to efficiently build data pipelines or calculations that require multiple distributed processes to collaborate around a broker.

By default, INI uses Kafka as a distributed broker for inter-process communication.

### Typical Uses/Applications

- IoT/Robotics
- Distributed Computing

### INI Main Features

- Process oriented: programmers can easily define processes that will run on INI nodes and react to events
- Reactive and event-driven: programmers can easily declare events to which processes will react
- Type inference and user types: programmers can define complex structured types and the type checker will enforce the correct usage of the structure
- Rule-based: processes and programs rely on rules for readability purpose
- Functional style: programmers familiar with functional programming can use functions in a minimalist way

## Getting started

Build with:

% cd {ini_root_dir}
% maven generate-sources # first build only (will generate INI parser)
% maven install

Lauch INI program (UNIX-based):

% cd {ini_root_dir}
% bin/ini {ini_file}

## Use with Kafka

In order to use the @consume event, you need Kafka up and running. For local testing, install and run Kafka as described on the official site.

% cd kafka_2.12-2.2.0
% bin/zookeeper-server-start.sh config/zookeeper.properties
% bin/kafka-server-start.sh config/server.properties

