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

### Sending messages with JmsTemplate

JmsTemplate has several methods that are useful for sending messages, including the following:

```java
interface JmsTemplate {
    // Send raw messages
    void send(MessageCreator messageCreator) throws JmsException;

    void send(Destination destination, MessageCreator messageCreator)
            throws JmsException;

    void send(String destinationName, MessageCreator messageCreator)
            throws JmsException;

    // Send messages converted from objects
    void convertAndSend(Object message) throws JmsException;

    void convertAndSend(Destination destination, Object message)
            throws JmsException;

    void convertAndSend(String destinationName, Object message)
            throws JmsException;

    // Send messages converted from objects with post-processing
    void convertAndSend(Object message,
                        MessagePostProcessor postProcessor) throws JmsException;

    void convertAndSend(Destination destination, Object message,
                        MessagePostProcessor postProcessor) throws JmsException;

    void convertAndSend(String destinationName, Object message,
                        MessagePostProcessor postProcessor) throws JmsException;
}
```

- Three send() methods require a MessageCreator to manufacture a Message object.
- Three convertAndSend() methods accept an Object and automatically convert that Object into a Message behind the
  scenes.
- Three convertAndSend() methods automatically convert an Object to a Message, but also accept a MessagePostProcessor to
  allow for customization of the Message before it’s sent.

- One method accepts no destination parameter and sends the message to a default destination.
- One method accepts a Destination object that specifies the destination for the message.
- One method accepts a String that specifies the destination for the message by name.

```java

@Service
public class JmsOrderMessagingServiceImpl implements JmsOrderMessagingService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Override
    public void SendMessage(TacoOrder order) {
        jmsTemplate.send(session -> session.createObjectMessage(order));
    }
}
```

But notice that the call to `jms.send()` doesn’t specify a destination. In order for this to work, you must also specify
a default destination name with the `spring.jms.template.default-destination` property. For example, you could set the
property in your application.yml file like this:

```yaml
spring:
  jms:
    template:
      default-destination: tacocloud.order.queue
```

One way of doing that is by passing a Destination object as the first parameter to send(). The easiest way to do this is
to declare a Destination bean and then inject it into the bean that performs messaging. For example, the following bean
declares the Taco Cloud order queue Destination:

```java
class Config {
    @Bean
    public Destination orderQueue() {
        return new ActiveMQQueue("tacocloud.order.queue");
    }
}
```

If this Destination bean is injected into JmsOrderMessagingService, you can use it to specify the destination when
calling send():

```java
class Service {
    @Autowired
    public JmsOrderMessagingService(JmsTemplate jms,
                                    Destination orderQueue) {
        this.jms = jms;
        this.orderQueue = orderQueue;
    }

    @Override
    public void sendOrder(Order order) {
        jms.send(
                orderQueue,
                session -> session.createObjectMessage(order));
    }
}
```

But in practice, you’ll almost never specify anything more than the destination name. It’s often easier to just send the
name as the first parameter to send():

```java
class service {
    @Override
    public void sendOrder(Order order) {
        jms.send(
                "tacocloud.order.queue",
                session -> session.createObjectMessage(order));
    }
}
```

#### CONVERTING MESSAGES BEFORE SENDING

`JmsTemplates’s convertAndSend()` method simplifies message publication by eliminating the need to provide a
`MessageCreator`. Instead, you pass the object that’s to be sent directly to `convertAndSend()`, and the object will be
converted into a Message before being sent. For example, the following reimplementation of `sendOrder()` uses
`convertAndSend()` to send an Order to a named destination:

```java
class Service {
    @Override
    public void sendOrder(Order order) {
        jms.convertAndSend("tacocloud.order.queue", order);
    }
}
```
