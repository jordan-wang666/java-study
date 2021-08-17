# Sending messages asynchronously

> **This chapter covers**
> - Asynchronous messaging
> - Sending messages with JMS, RabbitMQ, and Kafka
> - Pulling messages from a broker
> - Listening for messages

We’ll consider three options that Spring offers for asyn- chronous messaging: the Java Message Service (JMS), RabbitMQ
and Advanced Mes- sage Queueing Protocol (AMQP), and Apache Kafka. In addition to the basic sending and receiving of
messages, we’ll look at Spring’s support for message-driven POJOs: a way to receive messages that resembles EJB’s
message-driven beans (MDBs).

### Sending messages with JMS

*JMS* is a Java standard that defines a common API for working with message brokers. First introduced in 2001, JMS has
been the go-to approach for asynchronous messaging in Java for a very long time. Before *JMS*, each message broker had a
proprietary API, making an application’s messaging code less portable between brokers. But with JMS, all compliant
implementations can be worked with via a common interface in much the same way that JDBC has given relational database
operations a common interface.

Spring supports *JMS* through a template-based abstraction known as `JmsTemplate`. Using `JmsTemplate`, it’s easy to
send messages across queues and topics from the producer side and to receive those messages on the consumer side. Spring
also supports the notion of message-driven POJOs: simple Java objects that react to messages arriving on a queue or
topic in an asynchronous fashion.

We’re going to explore Spring’s JMS support, including `JmsTemplate` and message- driven POJOs. But before you can send
and receive messages, you need a message broker that’s ready to relay those messages between producers and consumers.
Let’s kick off our exploration of *Spring JMS* by setting up a message broker in Spring.

### Setting up JMS

If you’re using ActiveMQ, you’ll need to add the following dependency to your project’s pom.xml file:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

If ActiveMQ Artemis is the choice, the starter dependency should look like this:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```

Properties for configuring the location and credentials of an Artemis broker

```yaml
spring:
  artemis:
    host: artemis.tacocloud.com
    port: 61617
    user: tacoweb
    password: l3tm31n
```

Properties for configuring the location and credentials of an ActiveMQ broker

```yaml
spring:
  activemq:
    broker-url: tcp://activemq.tacocloud.com
    user: tacoweb
    password: l3tm31n
```

If you’re using ActiveMQ, you will, however, need to set the `spring.activemq.in-memory` property to false to prevent
Spring from starting an in-memory broker. An in-memory broker may seem useful, but it’s only helpful when you’ll be
consuming messages from the same application that publishes them (which has limited usefulness). Instead of using an
embedded broker, you’ll want to install and start an Artemis (or ActiveMQ) broker before moving on. Rather than repeat
the installation instructions here, I refer you to the broker documentation for details:

- Artemis—https://activemq.apache.org/artemis/docs/latest/using-server.html
- ActiveMQ—http://activemq.apache.org/getting-started.html#GettingStarted-Pre-
  InstallationRequirements
