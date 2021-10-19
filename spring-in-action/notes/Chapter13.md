# 13.Discovering services

> **This chapter covers**
> - Thinking in microservices
> - Creating a service registry
> - Registering and discovering services

### Thinking in microservices

Monolithic applications are deceptively simple, but they present a few challenges:

- Monoliths are difficult to reason about —The bigger the codebase gets, the harder it is to comprehend each component’s
  role in the whole application.
- Monoliths are more difficult to test—As the application grows, comprehensive integration and acceptance testing gets
  more complicated.
- Monoliths are more prone to library conflicts—One feature may require a dependency that’s incompatible with the
  dependency required by another.
- Monoliths scale inefficiently—If you need to deploy the application to more hardware for scaling purposes, you must
  deploy the entire application to more servers—even if it’s only a small fraction of the application that requires
  scaling.
- Technology decisions for a monolith are made for the entire monolith—When you choose a language, runtime platform,
  framework, or library for your application, you choose it for the entire application, even if the choice is made to
  only support a single use case.
- Monoliths require a great deal of ceremony to get to production—It would seem that when an application has only a
  single deployment unit, it would be easier to get into production. In reality, however, the size and complexity of
  monolithic applications generally require a more rigid development process and a more thorough testing cycle to ensure
  that what’s deployed is of high quality and doesn’t introduce bugs.

In the past few years, microservice architecture has risen to address these challenges. In simple terms, microservice
architecture is a way of factoring an application into small- scale, miniature applications that are independently
developed and deployed. These microservices coordinate with each other to provide the functionality of a greater
application. In contrast to monolithic application architecture, microservice architecture has these traits:

- Microservices can be easily understood—Each microservice has a small, finite con- tract with other microservices in
  the greater application. As a result, microservices are more focused in purpose and, therefore, easier to understand
  as a unit.
- Microservices are easier to test—The smaller something is, the easier it is to test. This is certainly evident when
  you consider unit testing versus integration test- ing versus acceptance testing. That also applies when testing
  microservices versus monolithic applications.
- Microservices are unlikely to suffer from library incompatibilities—Because each microservice has its own set of build
  dependencies that isn’t shared with other microservices, it’s less likely that there’ll be library conflicts.
- Microservices scale independently—If any given microservice needs more horse- power, then the memory allotment and/or
  the number of instances can be scaled up without impacting the memory or instance count of other microservices in the
  greater application.
- Technology choices can be made differently for each microservice—Entirely different decisions can be made with regard
  to the language, platform, framework, and library choices for each microservice. In fact, it’s entirely reasonable for
  one microservice written in Java to coordinate with another microservice written in C#.2
- Microservices can be published to production more frequently—Even though a micros- ervice-architected application is
  made up of many microservices, each one can be deployed without requiring that any of the other microservices also be
  deployed. And because they’re smaller, more focused, and easier to test, there’s less ceremony in taking a
  microservice into production. The time between having an idea and seeing it in production can potentially be measured
  in minutes and hours, instead of weeks and months.

### Setting up a service registry

#### WHY A CLIENT-SIDE LOAD BALANCER?

Often, load balancers are thought of as a single centralized service that handles all requests and distributes them
across many instances of the intended target. In contrast, Ribbon is a client-side load balancer that’s local to each
client making the requests.

As a client-side load balancer, Ribbon has several benefits over a centralized load balancer. Because there’s one load
balancer local to each client, the load balancer naturally scales proportional to the number of clients. Furthermore,
each load balancer can be configured to employ a load-balancing algorithm best suited for each client, rather than apply
the same configuration for all services.

Add Eureka

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

#### Configuring Eureka

```yaml
eureka:
  instance:
    hostname: localhost
  client:
    fetch-registry: false
    register-with-eureka: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
```

##### DISABLING SELF-PERSERVATION MODE

```yaml
eureka:
  server:
    enable-self-preservation: false

```

### Registering and discovering services

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### Configuring Eureka client properties

```yaml
 spring:
   application:
     name: ingredient-service
```

```yaml
 eureka:
   client:
     service-url:
       defaultZone: http://eureka1.tacocloud.com:8761/eureka/,
         http://eureka2.tacocloud.com:8762/eureka/
```

When the service starts, it attempts to register with the first server in the zone. If that fails for any reason, then
it attempts to register with the next one in the list. Eventually, when the failing Eureka comes back online, it
replicates the registry from its peer, containing a registry entry for the service.

#### Consuming services

It would be a mistake to hard code any service instance’s URL in the consumer’s code. This not only couples the consumer
to a specific instance of the service, but also can cause the consumer to break if the service’s host and/or port were
to change.

On the other hand, the consuming application has a lot of responsibility when it comes to looking up a service in
Eureka. Eureka may reply to the lookup with many instances for the same service. If the consumer asks for
ingredient-service and receives a half-dozen or so service instances in return, how does it choose the correct service?

The good news is that consuming applications don’t need to make that choice or even explicitly look up a service on
their own. Spring Cloud’s Eureka client support, along with the Ribbon client-side load balancer, makes it simple work
to look up, select, and consume a service instance. Two ways to consume a service looked up from Eureka include:

- A load-balanced RestTemplate
- Feign-generated client interfaces

##### CONSUMING SERVICES WITH RESTTEMPLATE

Once you’ve enabled an application as being a `Eureka` client, however, you have the option of declaring a
load-balanced `RestTemplate` bean. All you need to do is declare a regular `RestTemplate` bean, but annotate the `@Bean`
method with `@LoadBalanced`:

```java
class Bean {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

The `@LoadBalanced` annotation has two purposes: first, and most importantly, it tells Spring Cloud that this
RestTemplate should be instrumented with the ability to look up services through Ribbon. Secondly, it acts as an
injection qualifier, so that if you have two or more RestTemplate beans, you can specify that you want the load-balanced
RestTemplate at the injection point.

For example, suppose that you want to use the load-balanced RestTemplate to look up an ingredient as in the previous
code. First, you’d inject the load-balanced RestTemplate into the bean that needs it:

```java

@Component
public class IngredientServiceClient {
    private RestTemplate rest;

    public IngredientServiceClient(@LoadBalanced RestTemplate rest) {
        this.rest = rest;
    }
}
```

Then rewrite the `getIngredientById()` method slightly so that it uses the service’s registered name instead of an
explicit host and port:

```java
class Method {
    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject(
                "http://ingredient-service/ingredients/{id}",
                Ingredient.class, ingredientId);
    }
}
```

##### CONSUMING SERVICES WITH WEBCLIENT

```java
class Bean {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

```java

@Component
public class IngredientServiceClient {
    private WebClient.Builder wcBuilder;

    public IngredientServiceClient(
            @LoadBalanced WebClient.Builder webclientBuilder wcBuilder) {
        this.wcBuilder = wcBuilder;
    }
}
```

```java
class Method {
    public Mono<Ingredient> getIngredientById(String ingredientId) {
        return wcBuilder.build()
                .get()
                .uri("http://ingredient-service/ingredients/{id}", ingredientId)
                .retrieve().bodyToMono(Ingredient.class);
    }
}
```

#### DEFINING FEIGN CLIENT INTERFACES

Feign was originally a Netflix project, but has since been turned loose as an independent open-source project called
OpenFeign (https://github.com/OpenFeign). The word feign means “to pretend,” which you’ll soon see is an appropriate
name for a project that pretends to be a REST client.

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

This same starter dependency can be added automatically by checking the `Feign` checkbox when using Spring Initializr.
Unfortunately, there’s no autoconfiguration to enable Feign based on the existence of this dependency. Therefore, you’ll
need to add the `@EnableFeignClients` annotation to one of the configuration classes:

```java

@Configuration
@EnableFeignClients
public class RestClientConfiguration {
}
```

Now comes the fun part. Let’s say that you want to write a client that fetches an Ingredient from the service that’s
registered in Eureka as ingredient-service. The following interface is all you need:

```java

@FeignClient("ingredient-service")
public interface IngredientClient {
    @GetMapping("/ingredients/{id}")
    Ingredient getIngredient(@PathVariable("id") String id);
}
```

### Summary

- Spring Cloud Netflix enables the simple creation of a Netflix Eureka service registry with autoconfiguration and the
  @EnableEurekaServer annotation.
- Microservices register themselves by name with Eureka for discovery by other services.
- On the client-side, Ribbon acts as a client-side load balancer, looking up ser- vices by name and selecting an
  instance.
- Client code has the choice of either a RestTemplate that’s instrumented for Ribbon load balancing or defining its REST
  client code as interfaces that are implemented automatically at runtime by Feign.
- In any event, client code is not hard-coded with the location of the services that it consumes.