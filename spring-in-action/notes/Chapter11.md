# Developing reactive APIs

> **This chapter covers**
> - Using Spring WebFlux
> - Writing and testing reactive controllers and clients
> - Consuming REST APIs
> - Securing reactive web applications

### Working with Spring WebFlux

Typical Servlet-based web frameworks, such as Spring MVC, are blocking and multi- threaded in nature, using a single
thread per connection. As requests are handled, a worker thread is pulled from a thread pool to process the request.
Meanwhile, the request thread is blocked until it’s notified by the worker thread that it’s finished.

Consequently, blocking web frameworks won’t scale effectively under heavy request volume. Latency in slow worker threads
makes things even worse because it’ll take longer for the worker thread to be returned to the pool, ready to handle
another request. In some use cases, this arrangement is perfectly acceptable. In fact, this is largely how most web
applications have been developed for well over a decade. But times are changing.

The clients of those web applications have grown from people occasionally viewing websites to people frequently
consuming content and using applications that coordinate with HTTP APIs. And these days, the so-called Internet of
Things (where humans aren’t even involved) yields cars, jet engines, and other non-traditional clients constantly
exchanging data with web APIs. With an increasing number of clients consuming web applications, scalability is more
important than ever.

Asynchronous web frameworks, in contrast, achieve higher scalability with fewer threads—generally one per CPU core. By
applying a technique known as event looping (as illustrated in figure 11.1), these frameworks are able to handle many
requests per thread, making the per-connection cost more economical.

![img.png](../img/img39.png)

#### Introducing Spring WebFlux

As the Spring team was considering how to add a reactive programming model to the web layer, it quickly became apparent
that it would be difficult to do so without a great deal of work in Spring MVC. That would involve branching code to
decide whether to handle requests reactively or not. In essence, the result would be two web frameworks packaged as one,
with if statements to separate the reactive from the non-reactive.

Instead of trying to shoehorn a reactive programming model into Spring MVC, it was decided to create a separate reactive
web framework, borrowing as much from Spring MVC as possible. Spring WebFlux is the result. Figure 11.2 illustrates the
complete web development stack defined by Spring 5.

![img.png](../img/img40.png)

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

An interesting side-effect of using WebFlux instead of Spring MVC is that the default embedded server for WebFlux is
Netty instead of Tomcat. Netty is one of a handful of asynchronous, event-driven servers and is a natural fit for a
reactive web framework like Spring WebFlux.

Aside from using a different starter dependency, Spring `WebFlux` controller methods usually accept and return reactive
types, like `Mono` and `Flux`, instead of domain types and collections. Spring `WebFlux` controllers can also deal with
RxJava types like `Observable`, Single, and `Completable`.

##### REACTIVE SPRING MVC?

Although Spring `WebFlux` controllers typically return `Mono` and `Flux`, that doesn’t mean that Spring MVC doesn’t get
to have some fun with reactive types. Spring MVC controller methods can also return a `Mono` or `Flux`, if you’d like.

#### Writing reactive controllers

![img.png](../img/img41.png)

```java
class DesignTacoController {
    @GetMapping("/recent")
    public Flux<Taco> recentTacos() {
        return Flux.fromIterable(tacoRepo.findAll()).take(12);
    }
}
```

##### RETURNING SINGLE VALUES

```java
class DesignTacoController {
    @GetMapping("/{id}")
    public Mono<Taco> tacoById(@PathVariable("id") Long id) {
        return tacoRepo.findById(id);
    }
}
```

##### HANDLING INPUT REACTIVELY

```java
class Controller {
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepo.save(taco);
    }
}
```

### Defining functional request handlers

First, any annotation-based programming involves a split in the definition of what the annotation is supposed to do and
how it’s supposed to do it. Annotations themselves define the what; the how is defined elsewhere in the framework code.
This complicates the programming model when it comes to any sort of customization or extension because such changes
require working in code that’s external to the annotation. Moreover, debugging such code is tricky because you can’t set
a breakpoint on an annotation.

Also, as Spring continues to grow in popularity, developers new to Spring from other languages and frameworks may find
annotation-based Spring MVC (and Web- Flux) quite unlike what they already know. As an alternative to WebFlux, Spring 5
has introduced a new functional programming model for defining reactive APIs.

This new programming model is used more like a library and less like a framework, letting you map requests to handler
code without annotations. Writing an API using Spring’s functional programming model involves four primary types:

- RequestPredicate—Declares the kind(s) of requests that will be handled RouterFunction—Declares how a matching request
  should be routed to handler code
- ServerRequest—Represents an HTTP request, including access to header and body information
- ServerResponse—Represents an HTTP response, including header and body information

As a simple example that pulls all of these types together, consider the following Hello World example:

```java
import static org.springframework.web.
        reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.
        reactive.function.server.RouterFunctions.route;
import static org.springframework.web.
        reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"),
                request -> ok().body(just("Hello World!"), String.class));
    }
}
```

As written, the `helloRouterFunction()` method declares a `RouterFunction` that only handles a single kind of request.
But if you need to handle a different kind of request, you don’t have to write another `@Bean` method, although you can.
You only need to call `andRoute()` to declare another RequestPredicate-to-function mapping. For example, here’s how you
might add another handler for GET requests for /bye:

```java
class Config {
    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"),
                request -> ok().body(just("Hello World!"), String.class))
                .andRoute(GET("/bye"),
                        request -> ok().body(just("See ya!"), String.class));
    }
}
```

To demonstrate how the functional programming model might be used in a real world application, let’s reinvent the
functionality of `DesignTacoController` in the functional style. The following configuration class is a functional
analog to `DesignTacoController`:

```java

@Configuration
public class RouterFunctionConfig {
    @Autowired
    private TacoRepository tacoRepo;

    @Bean
    public RouterFunction<?> routerFunction() {
        return route(GET("/design/taco"), this::recents)
                .andRoute(POST("/design"), this::postTaco);
    }

    public Mono<ServerResponse> recents(ServerRequest request) {
        return ServerResponse.ok()
                .body(tacoRepo.findAll().take(12), Taco.class);
    }

    public Mono<ServerResponse> postTaco(ServerRequest request) {
        Mono<Taco> taco = request.bodyToMono(Taco.class);
        Mono<Taco> savedTaco = tacoRepo.save(taco);
        return ServerResponse
                .created(URI.create(
                        "http://localhost:8080/design/taco/" +
                                savedTaco.getId()))
                .body(savedTaco, Taco.class);
    }
}
```