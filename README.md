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
### The configuration files
As for any other **Spring Boot Application** we  use a configuration file to set the values of the properties other than the default for **config-service**: 
```
server.port=8888
spring.application.name=config-service
spring.profiles.active=native
```
 For now the 2 important  configuration's properties for **config-service** are:
 + *server.port*: to get access to the configuration data saved in the repository amnaged by **config-service**, a microservice need this value. 
 + *spring.profiles.active*: its value must be set to *native* to tell **config-service** to use a file system repository. Per default **Spring Cloud Config Server** searches for files stored in the following locations, *classpath:/, classpath:/config, file:./, file:./config*. If we want to use another location on our computer we can use *spring.cloud.config.server.native.searchLocations*.    
### The file system repository
Now we need to add configuration data for the  **employee-service** and **department-service** to the file system repository. To let things simple we add a *config* folder under *src/main/resources*. Furthermore we add the following configurationfiles:
+ department-service-dev.properties
+ department-service-prod.properties
+ employee-service-dev.properties
+ employee-service-prod.properties
For now these files containt just the port number for the related microservice:
```
# department-service-dev.properties
server.port: 8082

# department-service-prod.properties
server.port: 9082

# employee-service-dev.properties
server.port: 8081

# employee--service-prod.properties
server.port: 9081
```
If we start the  **config-service** and request http://localhost:8888/employee-service/dev 
 
.is in the following 
 


### Summary
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTg3MDI1ODA3Myw2Nzc1Nzg3NTksLTE4OT
YyNTM2MywxOTAzOTA4Mjg3LDEwMDU4NTI3NjEsMjAzMDk0MjY2
OCwyMTM0MjUzNzgxLDIwNzI3ODUyMzMsMjE3NDA0NzY3LDM2NT
U2OTE2NSwtNjc3MzU5ODQyLDE1MjcxNzY2MTksMTg4NzA2MzQw
LDE5MjAxMTUyNTZdfQ==
-->