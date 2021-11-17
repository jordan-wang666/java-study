# 15.Handling failure and latency

> **This chapter covers**
> - Introducing the circuit breaker pattern
> - Handling failure and latency with Hystrix - Monitoring circuit breakers
> - Aggregating circuit breaker metrics

### Understanding circuit breakers

The following categories of methods are certainly candidates for circuit breakers:

- Methods that make REST calls—These could fail due to the remote service being unavailable or returning HTTP 500
  responses.
- Methods that perform database queries—These could fail if, for some reason, the database becomes unresponsive, or if
  the schema changes in ways that break the application.
- Methods that are potentially slow—These won’t necessarily fail, but may be considered unhealthy if they're taking too
  long to do their job.

### Declaring circuit breakers

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

With the `Hystrix` starter dependency in place, the next thing you’ll need to do is to enable `Hystrix`. The way to do
that is to annotate each application’s main configuration class with `@EnableHystrix`. For example, to enable `Hystrix`
in the ingredient service, you’d annotate `IngredientServiceApplication` like this:

```java

@SpringBootApplication
@EnableHystrix
public class IngredientServiceApplication {

}
```

Uncaught exceptions are a challenge in any application, but especially so in microservices. When it comes to failures,
microservices should apply the Vegas Rule—what happens in a microservice, stays in a microservice. Declaring a circuit
breaker on the `getAllIngredients()` method satisfies that rule.

At a minimum, you only need to annotate the method with `@HystrixCommand`, and then provide a fallback method. First,
let’s add `@HystrixCommand` to the `getAllIngredients()` method:

```java
class Hystrix {
    @HystrixCommand(fallbackMethod = "getDefaultIngredients")
    public Iterable<Ingredient> getAllIngredients() {
    }
}
```

At this point, Hystrix is enabled in your application. But that only means that all the pieces are in place for you to
declare circuit breakers. You still haven’t declared any circuit breakers on any of the methods. That’s where
the `@HystrixCommand` annotation comes into play.

### Mitigating latency

```java
class Test {
    @HystrixCommand(
            fallbackMethod = "getDefaultIngredients",
            commandProperties = {
                    @HystrixProperty(
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "500")
            })
    public Iterable<Ingredient> getAllIngredients() {

    }
}
```

```java
class Test {
    @HystrixCommand(
            fallbackMethod = "getDefaultIngredients",
            commandProperties = {
                    @HystrixProperty(
                            name = "execution.timeout.enabled",
                            value = "false")
            })
    public Iterable<Ingredient> getAllIngredients() {
    }
}
```

### Managing circuit breaker thresholds

You can tweak the failure and retry thresholds by setting the Hystrix command properties. The following command
properties influence the conditions that result in a circuit breaker being thrown:

- circuitBreaker.requestVolumeThreshold—The number of times a method should be called within a given time period
- circuitBreaker.errorThresholdPercentage—A percentage of failed method invocations within a given time period
- metrics.rollingStats.timeInMilliseconds—A rolling time period for which the request volume and error percentage are
  considered
- circuitBreaker.sleepWindowInMilliseconds—How long an open circuit remains open before entering a half-open state and
  the original failing method is attempted again

## Monitoring failures

Every time a circuit breaker protected method is invoked, several pieces of data are collected about the invocation and
published in an HTTP stream that can be used to monitor the health of the running application in real time. Among the
data collected for each circuit breaker, the `Hystrix` stream includes the following:

- How many times the method is called
- How many times it’s called successfully
- How many times the fallback method is called
- How many times the method times out

The Hystrix stream is provided by an Actuator endpoint. We’ll talk more about Actuator in chapter 16. But, for now, the
Actuator dependency needs to be added to the build for all the services to enable the Hystrix stream. In a Maven pom.xml
file, the following starter dependency adds Actuator to a project:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Summary

- The circuit breaker pattern enables graceful failure handling.
- Hystrix implements the circuit breaker pattern, enabling fallback behavior when a method fails or is too slow.
- Each circuit breaker provided by Hystrix publishes metrics in a stream of data for purposes of monitoring the health
  of an application.
- The Hystrix stream can be consumed by the Hystrix dashboard, a web application that visualizes circuit breaker
  metrics.
- Turbine aggregates multiple Hystrix streams from multiple applications into a single stream that can be visualized
  together in the Hystrix dashboard.