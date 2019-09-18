
## About INI

When *industrializing Big Data and Machine Learning* (especially Deep Learning), *one major concern is to build data pipelines* that allow the data to be transmitted from various sources (IoT devices, mobile/web applications, ERP databases, ...) to various targets (datalakes, Jupiter Notebooks, Hadoop FS, ...). All these Big Data pipelines require tremendous efforts in terms of infrastructure, but also in terms of development, deployment, and maintenance. 

*INI* is a scripting language that aims at filling a gap by providing *a simple way to prototype, implement, and deploy pipelines* and distributed streaming computations efficiently. INI has been designed to keep distributed computing as simple as it can be. *It natively handles processes, deployment, communication and synchronization*.

INI is not meant to be a general-purpose language, nor a low-level performance-driven language. It does not address all programming issues programmers can face. On the other hand, it helps programmers building and deploying distributed data pipelines or calculations.

By default, INI uses *Kafka* as a broker.

### Language Design Philosophy

INI is built around two first-class entities.

- *Functions*, as in *functional programming*, used for pure and safe calculations.
- *Processes*, which are asynchronous, multi-threaded, rule-based, and react to their environment through events (as in *reactive programming*).
 
### Typical Uses/Applications

- Pipelines for Big Data/ML/DL
- IoT/Robotics
- Distributed Computing
- Multi-Agent Systems
- Critical Systems

### INI Main Features

- Process oriented: programmers can define processes that will run on INI nodes.
- Reactive and event-driven: processes react to events, including consume events that allow for inter-process communication.
- Rule-based: processes and functions rely on rules for readability purpose.
- Functional style: programmers familiar with functional programming can use functions and recursion.
- Auto-deployment: processes and functions are automatically deployed with annotations, either in push or in pull mode.
- Type inference: programs are strongly typed-checked with type inference (still under development).

## Quick start

Requirements: Java 1.8+, Apache Maven (in your path)

Build with:

```console
$ cd {ini_root_dir}
$ mvn package -Dmaven.test.skip=true
```

Start an INI shell (UNIX-based OS):

```console
$ cd {ini_root_dir}
$ bin/ini --shell
>
```

Basic command-line use with INI shell:

```console
> s = "hello world" # defines a string variable 's'
hello world
> println(s) # prints out the content of s
hello world
> l = [1..4] # defines a list containing 4 integers
[1..4]
> inc = i => i+1 # defines a lambda that increments an integer
<lambda>(i)
> import "ini/lib/collect.ini" # imports the lib to support function about collections
select(l,function)
> l.map(inc) # increments all the integers in 'l'
[2,3,4,5]
```

Launch INI program (UNIX-based OS):

```console
$ cd {ini_root_dir}
$ bin/ini {ini_file}
```

## Examples

The goal of these examples is to give a first overview of the INI syntax and semantics. Download the full language specifications [here](https://github.com/cincheo/ini/raw/master/doc/ini_language_specs/ini_language_specs.pdf).

### A factorial function

For pure local calculations, INI programmers can define functions. Here is a typical factorial calculation with INI. 

```javascript
function fac(n) {
  case n == 1 {
    return 1
  }
  case n > 1 {
    return n * fac(n-1)
  }
}

// prints out the result of the function
function main(args) {
  println(fac(to_int(args[0]))
}
```

To run the program, simply save it in a file (for instance ``fac.ini``), and type in:

```console
$ bin/ini fac.ini 3
6
```

### A factorial process (rule-based style)

On contrary to a function, a process runs asynchronously and can only contain rules. In an INI process, all the rules continue to be executed until none is applicable anymore or if the process has returned a value (using an explicit ``return`` statement). For instance, here is the factorial implementation with a process (rule-based style).

```javascript
process fac(n) {
  @init() {
    f=1
    i=2
  }
  // this rule will loop until i > n
  i <= n {
    f=f*i++
  }
  @end() {
    return f
  }
}

// prints out the result of the function
function main(args) {
  println(fac(to_int(args[0]))
}
```

The second rule, guarded by ``i <= n``, continues to be executed until the ``i`` variable value become greater than ``n``. In INI, looping in processes can be implemented this way, which means that there is no need for having loops as control flow constructs in the language. This design choice keeps the language small and simple, thus making INI programs usually easy to read and maintain.

Finally, note the ``@init`` and ``@end`` rules, which are called "event rules". The ``@init`` event is a one-shot event that is evaluated before all other rules, while the ``@end`` event is a one-shot event evaluated once no rules are left to be applied. 

**Note**: it is important to understand that INI processes are run asynchronously. They do not interrupt the thread of the function/process invoking them unless the invoking function/process reads the result of the invoked process. In that case, the invoking function/process waits until the invoked process returns a value. Under the hood, behaves this way because processes return future values instead of actual values. In our example, the ``main`` function waits for the ``fac`` result because it uses it as an argument of the ``println`` function.

### A process awaking every second

In INI, a process reacts to its environment through events. By default, a process will never end, unless none of the rules can apply anymore and all the events are terminated. 

The following INI program creates a process that will be notified every second (1000ms) by the ``@every`` event. It then evaluate the rule's action to print a tick and increments the tick count hold by the ``i`` variable.

```javascript
process main() {
  @init() {
    i = 1
  }
  @every() [time=1000] {
    println("tick "+(i++))
  }
}
```

Note the ``[time=1000]`` construct, which configures the ``@every`` event rule to be fired every second. This construct will be commonly used in INI programs and is called an *annotation*.

### A simple 3-process data pipeline

The basics of process communication is provided by *channels* (similarly to Pi calculus and most agent-based systems). Processes can produce data in channels using the ``produce`` function, and consume data from channel using the ``@consume`` event.

In the following program, the ``main`` process creates two sub-processes by calling ``p``. Each sub-process consumes a data from an ``in`` channel and produces the incremented result to an ``out`` channel. Thus, it creates a pipeline that ultimately sends back the data incremented twice to the main process.

```javascript
process main() {
  @init() {
    p("c1", "c2")
    p("c2", "c")
    println("processes started")
    produce("c1", 1)
  }
  @consume(v) [channel="c"] {
    println("end of pipeline: "+v)
  }
}

process p(in, out) {
  @consume(v) [channel=in] {
    println(in+": "+v)
    produce(out, v+1)
  }
}
```

The above program behaves as depicted here:

- ``main`` creates two sub-processes ``p("c1", "c2")`` and ``p("c2", "c")``,
- ``main`` sends the data ``1`` to the ``"c1"`` channel (``produce("c1", 1)``),
- ``1`` is consumed from ``"c1"`` by ``p("c1", "c2")``, and ``2`` is produced to ``"c2"``,
- ``2`` is consumed from ``"c2"`` by ``p("c2", "c")``, and ``3`` is produced to ``"c"``,
- finally, ``3`` is consumed from ``"c"`` by ``main``, and the pipeline stops there.

## Distributed mode

So far, we have shown the INI capabilities without taking into account deployment on remote machines nor distributed communication between them. So, all the previous examples execute locally, on a single INI instance. Below, we explain how to use INI to support easy deployment and communication amongst an arbitrary number of INI nodes distributed across the network.

### Broker configuration

First install and start Apache Kafka:

```console
$ cd kafka_{version}
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
```

Launch an INI node:

```console
$ cd {ini_root_dir}
$ bin/ini -n {node_name} {ini_file} # alternatively set the INI_NODE environment variable
```

For development (JUnit tests), INI uses the ``development`` environment, which defaults to a locally-installed Kafka broker. In order to use another Kafka instance, modify the ``ini_config.json`` configuration file to set the right connection parameters.

Ultimately, once moving an INI program to production, you should modify the ``production`` environment to connect to the production Kafka broker. Then, select the ``production`` environment by setting the ``INI_ENV`` system environment variable to ``production``, or by using the ``--env`` option when running INI.

### INI nodes and auto-deployment

By default processes or functions are started on the current node (as given by the ``-n`` starting option). However, by simply using annotations, the programmer can decide on which (remote) INI node the process or functions shall be executed. There are two ways to deploy processes or functions remotely:

- Push the process/function on a remote node.
- Pull the process/function from a remote node.

### Push/spawn a process on a target node

Given the pipeline example explained above, to push/spawn the ``p`` processes to nodes ``n1`` and ``n2`` (assuming that these nodes have been properly launched), we just add the ``node`` annotation when starting the processes. Additionally, we also need to prefix the names of the channels with ``+``. By default, channels remain local to the current process and this prefix is required so that the channels become visible by all nodes (through the Kafka broker).

```javascript
process main() {
  @init() {
    p("+c1", "+c2") [node="n1"]
    p("+c2", "+c")  [node="n1"]
    println("processes started")
    produce("+c1", 1)
  }
  @consume(v) [channel="+c"] {
    println("end of pipeline: "+v)
  }
}

process p(in, out) ... // unchanged
```

In that case, the program of the ``main`` node acts like a coordinator for all the other processes in the distributed program.

A key point to remember is that when a process is spawned to a remote node, the required code (processes and functions) will be automatically fetched from the spawning node. So there is no need for the programmer to pre-deploy manually any piece of program on the INI nodes. INI will take care of all this transparently.

### Pull from a server node

In other cases, a given node may want to evaluate a function or a process that has been defined in another node. In that case, the target node may pull a function or a process from a remote server (any INI node). To do so, the target node needs to declare a function/process binding annotated with the right server node. For instance, the following program runs the ``hello(string)`` function, which has been defined on a node called ``server``. In that case, the ``hello`` method implementation is fetched from the server node and evaluated locally on the requesting node. 

```javascript
// this is a binding to a function declared on a remote server
hello(String)=>String [node="server"]

function main() {
  println(hello("Renaud"))
}
``` 

Note that the binding of ``hello``, also defines the functional type ``(String) => String``, since INI cannot infer it from the function implementation.


