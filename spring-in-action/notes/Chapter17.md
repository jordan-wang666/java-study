# 17.Administering Spring

### Summary

- The Spring Boot Admin server consumes the Actuator endpoints from one or more Spring Boot applications and presents
  the data in a user- friendly web application.
- Spring Boot applications can either register themselves as clients to the Admin server or the Admin server can
  discover them through Eureka.
- Unlike the Actuator endpoints that capture a snapshot of an application’s state, the Admin server is able to display a
  live view into the inner workings of an application.
- The Admin server makes it easy to filter Actuator results and, in some cases, dis- play data visually in a graph.
- Because it’s a Spring Boot application, the Admin server can be secured by any means available through Spring
  Security.