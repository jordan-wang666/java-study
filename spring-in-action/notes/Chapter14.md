# 14.Managing configuration

> **This chapter covers**
> - Running Spring Cloud Config Server
> - Creating Config Server clients
> - Storing sensitive configuration
> - Automatically refreshing configuration

### Sharing configuration

In contrast, consider how those scenarios play out when configuration management is centralized:

- Configuration is no longer packaged and deployed with the application code, making it possible to change or roll back
  configuration without rebuilding or redeploying the application. Configuration can be changed on the fly without even
  restarting the application.
- Microservices that share common configuration needn’t manage their own copy of the property settings and can share the
  same properties. If changes to the properties are required, those changes can be made once, in a single place, and
  applied to all microservices.
- Sensitive configuration details can be encrypted and maintained separate from the application code. The unencrypted
  values can be made available to the application on demand, rather than requiring the application to carry code that
  decrypts the information.

### Enabling Config Server

I’m inclined to name the new project “config-server”, but you’re welcome to name it however you wish. The most important
thing to do is to specify the Config Server dependency by checking the Config Server check box. This will result in the
following dependency being added to the produced project’s pom.xml file:

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

Although the Config Server dependency adds Spring Cloud to the project’s classpath, there’s no autoconfiguration to
enable it, so you’ll need to annotate a configuration class with `@EnableConfigServer`. This annotation, as its name
implies, enables a Con- fig Server when the application is running. I usually just drop `@EnableConfigServer` on the
main class like so:

```java

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

There’s only one more thing that must be done before you can fire up the application and see the Config Server at work:
you must tell it where the configuration properties that it’s to serve can be found. To start, you’ll use configuration
that’s served from a Git repository, so you’ll need to set the spring.cloud.config.server.git.uri property with the URL
of the configuration repository:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/tacocloud/tacocloud-config
```

### Consuming shared configuration

The easiest way to turn any Spring Boot application into a Config Server client is to add the following dependency to
the project’s Maven build:

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

When the application is run, autoconfiguration will kick in to automatically regis- ter a property source that draws its
properties from a Config Server. By default, it assumes that the Config Server is running on localhost and listening on
port 8888. But if that’s not the case, you can configure the location of the Config Server by setting
the `spring.cloud.config.uri` property:

> **Which comes first: the Config Server or the Service Registry?**<br>
> You’re setting up your microservices to learn about the Eureka service registry from the Config Server. This is a common approach to avoid propagating service registry details across every single microservice in an application.<br>
> Alternatively, it’s possible to have the Config Server register itself with Eureka and have each microservice discover the Config Server as it would any other service. If you prefer this model, you’ll need to configure the Config Server as a discovery client and set the spring.cloud.config.discovery.enabled property to true. As a result, the Config Server will register itself in Eureka with the name “configserver.”<br>
> The downside of this approach is that each service will need to make two calls at startup: one to Eureka to discover the Config Server, followed by one to Config Server to fetch configuration data.

### Refreshing configuration properties on the fly

Traditionally, application maintenance, including configuration changes, has required that an application be redeployed
or at least restarted. The application would need to be brought back to the gate, so to speak, for lack of a mech droid
to adjust even the smallest configuration property. But that’s unacceptable for cloud- native applications. We’d like to
be able to change configuration properties on the fly, without even bringing the application down.

Fortunately, Spring Cloud Config Server supports the ability to refresh configura- tion properties of running
applications with zero downtime. Once the changes have been pushed to the backing Git repository or Vault secret store,
each microservice in the application can immediately be refreshed with the new configuration in one of two ways:

- Manual—The Config Server client enables a special Actuator endpoint at /actuator/refresh. An HTTP POST request to that
  endpoint on each service will force the config client to retrieve the latest configuration from its backends.
- Automatic—A commit hook in the Git repository can trigger a refresh on all ser- vices that are clients of the Config
  Server. This involves another Spring Cloud project called Spring Cloud Bus for communicating between the Config Server
  and its clients.

#### Manually refreshing configuration properties

Whenever you enable an application to be a client of the Config Server, the autoconfiguration in play also configures a
special Actuator endpoint for refreshing configuration properties. To make use of this endpoint, you’ll need to include
the Actuator starter dependency along with the Config Client dependency in your project’s build:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### Automatically refreshing configuration properties

The property refresh process shown can be summarized like this:

- A webhook is created on the configuration Git repository to notify Config Server of any changes (such as any pushes)
  to the Git repository. Webhooks are supported by many Git implementations, including GitHub, GitLab, Bitbucket, and
  Gogs.
- Config Server reacts to webhook POST requests by broadcasting a message regarding the change by way of a message
  broker, such as RabbitMQ or Kafka.
- Individual Config Server client applications subscribed to the notifications react to the notification messages by
  refreshing their environments with new property values from the Config Server.

There are several moving parts in play when using automatic property refresh with Config Server. Let’s review the
changes that you’re about to make to get a high-level understanding of what needs to be done:

- You’ll need a message broker available to handle the messaging between Config Server and its clients. You may choose
  either RabbitMQ or Kafka.
- A webhook will need to be created in the backend Git repository to notify Con- fig Server of any changes.
- Config Server will need to be enabled with the Config Server monitor depen- dency (which provides the endpoint that
  will handle webhook requests from the Git repository) and either the RabbitMQ or Kafka Spring Cloud Stream
  dependency (for publishing property change messages to the broker).
- Unless the message broker is running locally with the default settings, you’ll need to configure the details for
  connecting to the broker in both the Config Server and in all of its clients.
- Each Config Server client application will need the Spring Cloud Bus dependency.

### Summary

- Spring Cloud Config Server offers a centralized source of configuration data to all microservices that make up a
  larger microservice-architected application.
- The properties served by Config Server are maintained in a backend Git or Vault repository.
- In addition to global properties, which are exposed to all Config Server clients, Config Server can also serve
  profile-specific and application-specific properties.
- Sensitive properties can be kept secret by encrypting them in a backend Git repository or by storing them as secrets
  in a Vault backend.
- Config Server clients can be refreshed with new properties either manually via an Actuator endpoint or automatically
  with Spring Cloud Bus and Git webhooks.