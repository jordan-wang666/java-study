# 19. Deploying Spring

> **This chapter covers**
> - Building Spring applications as either WAR or JAR files
> - Pushing Spring applications to Cloud Foundry
> - Containerizing Spring applications with Docker

### Weighing deployment options

- Running the application in the IDE with either Spring Tool Suite or IntelliJ IDEA
- Running the application from the command line using the Maven springboot:run goal or Gradle bootRun task
- Using Maven or Gradle to produce an executable JAR file that can be run at the command line or deployed in the cloud
- Using Maven or Gradle to produce a WAR file that can be deployed to a traditional Java application server

In general, the choice comes down to whether you plan to deploy your application to a traditional Java application
server or to a cloud platform:

- Deploying to Java application servers—If you must deploy your application to Tom- cat, WebSphere, WebLogic, or any
  other traditional Java application server, you really have no choice but to build your application as a WAR file.
- Deploying to the cloud—If you’re planning to deploy your application to the cloud, whether it be Cloud Foundry, Amazon
  Web Services (AWS), Azure, Goo- gle Cloud Platform, or most any other cloud platform, then an executable JAR file is
  the best choice. Even if the cloud platform supports WAR deployment, the JAR file format is much simpler than the WAR
  format, which is designed for application server deployment.

### Summary

- Spring applications can be deployed in a number of different environments, including traditional application servers,
  platform-as-a-service (PaaS) environments like Cloud Foundry, or as Docker containers.
- When building a WAR file, you should include a class that subclasses SpringBootServletInitializr to ensure that
  Spring’s DispatcherServlet is properly configured.
- Building as an executable JAR file allows a Spring Boot application to be deployed to several cloud platforms without
  the overhead of a WAR file.
- Containerizing Spring applications is as simple as using Spotify’s Dockerfile plugin for Maven. It wraps an executable
  JAR file in a Docker container that can be deployed anywhere Docker containers can be deployed, including cloud
  providers such as Amazon Web Services, Microsoft Azure, Google Cloud Platform, Pivotal Web Services (PWS), and Pivotal
  Container Service (PKS).