# Microservices Tutorial-004
# Let our microservices be independents
## Introduction
In the last [tuturial-003](https://github.com/Meziano/tutorial-003) we have hardcoded the URL of the **employee-service** in the **department-service** and this is not very wise: What if the used port changes? What if we have 10 or more microservices that need to communicate with each other and that must have at least differents server-port on different environments like development, test and production?
It's abvious that it makes no sense it make sense to have to manually update each and every one of these services. 
The idea is to keep the configuration in a centralized and easy to access place, so that each microservice can load its own configuration and refresh it automatically to reflect any changes without restarting the JVM.
> A Spring Cloud Server provides server and client-side support for externalized configuration in a distributed system. With the Config Server you have a central
> place to manage external properties for applications across all environments.
In this tutorial we will use a [Spring Cloud Config server](https://cloud.spring.io/spring-cloud-config/) to externally store variables our  application will need to run in all environments.
So we will use our **employee-service** and **department-service** projects and add a **config-service** as an "infrastructure" microservice that manages configuration data of the other microservices.
## The BOM for dependency management
As we will use classes, interfaces, annotations and the like form the Spring Cloud in our child projects, we have to add the following *BOM* to the parent project.
```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>${spring-cloud.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
## The config-service 
The **config-service** is a usual **Spring Boot Application** with the extra dependency 
```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
Per default the *Config Server* manages a Github repository.
To keep it simple we will use a filesystem repository. 
### The ConfigApplication
The *ConfigApplication* must be annotated with *@EnableConfigServer* to add an embedded **Config Server** to our application
```
package de.meziane.ms;
...
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {
  public static void main( String[] args ) {
    SpringApplication.run(ConfigApplication.class, args);    	
  }
}
```
### The configuration file
As for any other **Spring Boot Application** we  use a configuration file to set the values of the properties other than the default for **config-service**.  
```
server.port=8888
spring.application.name=config-service
spring.profiles.active=native

```
### The filesystem repository



### Summary
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTQ4MTUzMTgxNCwxMDA1ODUyNzYxLDIwMz
A5NDI2NjgsMjEzNDI1Mzc4MSwyMDcyNzg1MjMzLDIxNzQwNDc2
NywzNjU1NjkxNjUsLTY3NzM1OTg0MiwxNTI3MTc2NjE5LDE4OD
cwNjM0MCwxOTIwMTE1MjU2XX0=
-->