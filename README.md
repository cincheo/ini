
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
- Functional style: programmers familiar with functional programming can use functions and recursion

## Examples

The following INI program creates a process that will be notified every 1000 ms by the @every event. It will then apply the rule to print a tick and increment the tick count hold by the i variable.

```javascript
process main() {
	@init() {
		i = 1
	}
	@every[time=1000]() {
		println("tick "+(i++))
	}
}
```

In the following program, the main process creates two sub-processes p1 and p2. These 3 processes produce and consumes in 3 channels in order to create a pipeline that will increment the transmitted value twice.

> main --c1--> p1 --c2--> p2 --c--> main


```javascript
agent main() {
	@init() {
		p1("c1")
		p2("c2")
		println("processes started")
		produce("c1", 1)
	}
	@consume[channel="c"](v) {
		println("end of pipeline: "+v)
	}
}

agent p1(name) {
	@consume[channel="c1"](v) {
		println(name+": "+v)
		produce("c2", v+1)
	}
}

agent p2(name) {
	@consume[channel="c2"](v) {
		println(name+": "+v)
		produce("c", v+1)
	}
}
```

## Getting started

Install and start Apache Kafka:

```console
$ cd kafka_{version}
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
```

Build with:

```console
$ cd {ini_root_dir}
$ maven generate-sources # first build only (will generate INI parser)
$ maven install
```

Lauch INI program (UNIX-based):

```console
$ cd {ini_root_dir}
$ bin/ini {ini_file}
```

## Using with Kafka

For development (JUnit tests), INI uses the "development" environment, which uses a locally installed Kafka broker. 
In order to use another Kafka instance, modify the "ini_config.json" configuration file to set the right connection parameters. Typically once moving an INI program to production, you should modify the "production" environment to connect to the production Kafka instance. Then you should ask INI to use the production environment by setting the "INI_ENV" system environment variable to "production", or by using the "--env" option when running INI.

## Origins of INI

INI was first created and design by Renaud Pawlak as a side research project.

It was extended by Truang Giang Lee during his PhD to introduce better control and synchronization on events, as well as formal verification of INI programs using model checking. His PhD was co-supervised by Renaud Rioboo (research director), Renaud Pawlak, Mathieu Manceny, and Olivier Hermant.

