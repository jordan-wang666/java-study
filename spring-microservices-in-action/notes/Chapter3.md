# Controlling your configuration with Spring Cloud configuration server

> **This chapter covers**
> - Separating service configuration from service code
> - Configuring a Spring Cloud configuration server
> - Integrating a Spring Boot microservice
> - Encrypting sensitive properties

### Building our Spring Cloud configuration server

Config local properties

```yaml
server:
  port: 8888
spring:
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: /Users/wangxi/Projects/java-study/spring-microservices-in-action/config-server-demo/src/main/resources
```

When `Spring Cloud Configuration Server` was started

```
localhost:8888/licensingservice/default
```

Will show the properties(`licensingservice` is property's name),seams like this:

```json
{
  "name": "licensingservice",
  "profiles": [
    "default"
  ],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "file:/Users/wangxi/Projects/java-study/spring-microservices-in-action/config-server-demo/src/main/resources/licensingservice.yml",
      "source": {
        "tracer.property": "I AM THE DEFAULT",
        "spring.jpa.database": "POSTGRESQL",
        "spring.datasource.platform": "postgres",
        "spring.jpa.show-sql": "true",
        "spring.database.driverClassName": "org.postgresql.Driver",
        "spring.datasource.url": "jdbc:postgresql://database:5432/eagle_eye_local",
        "spring.datasource.username": "postgres",
        "spring.datasource.password": "p0stgr@s",
        "spring.datasource.testWhileIdle": "true",
        "spring.datasource.validationQuery": "SELECT 1",
        "spring.jpa.properties.hibernate.dialect": "org.hibernate.dialect.PostgreSQLDialect"
      }
    },
    {
      "name": "file:/Users/wangxi/Projects/java-study/spring-microservices-in-action/config-server-demo/src/main/resources/application.yml",
      "source": {
        "server.port": 8888,
        "spring.profiles.active": "native",
        "spring.cloud.config.server.native.search-locations": "/Users/wangxi/Projects/java-study/spring-microservices-in-action/config-server-demo/src/main/resources"
      }
    }
  ]
}
```

When the Spring Framework does property resolution, it will always look for the property in the default properties first
and then override the default with an environment-specific value if one is present.

#### Configuring the licensing service to use Spring Cloud Config

Create a `bootstrap.yml` in client server

```yaml
spring:
  application:
    name: licensingservice
  profiles:
    active: default
  cloud:
    config:
      uri: http://localhost:8888
```