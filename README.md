# Microservices Tutorial-004
# Let our microservices be independents
## Introduction
In the last [tuturial-003](https://github.com/Meziano/tutorial-003) we have hardcoded the URL of the **employee-service** in the **department-service** and this is not very wise: What if the used port changes? What if we have 10 or more microservices that need to communicate with each other and that must have at least differents server-port on different environments like development, test and production?
It's abvious that it makes no sense it make sense to have to manually update each and every one of these services. 
The idea is to keep the configuration in a centralized and easy to access place, so that each microservice can load its own configuration and refresh it automatically to reflect any changes without restarting the JVM.
> A Spring Cloud Server provides server and client-side support for externalized configuration in a distributed system. With the Config Server you have a central
> place to manage external properties for applications across all environments.

In this tutorial we will use a [Spring Cloud Config server](https://cloud.spring.io/spring-cloud-config/) to externally store variables our  application will need to run in all environments.
So we will use our **department-service** and **employee-service** projects and add a **config-service** as an "infrastructure" microservice that manages configuration data of the other microservices.
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
 + *server.port*: to get access to the configuration data saved in the repository managed by **config-service**, a microservice need this value. 
 + *spring.profiles.active*: its value must be set to *native* to tell **config-service** to use a file system repository. Per default **Spring Cloud Config Server** searches for files stored in the following locations, *classpath:/, classpath:/config, file:./, file:./config*. If we want to use another location on our computer we can use *spring.cloud.config.server.native.searchLocations*.    
### The file system repository
Now we need to add configuration data for the  **employee-service** and **department-service** to the file system repository. To let things simple we add a *config* folder under *src/main/resources*. 


![The local repository](images/localRepoWithConfigFiles.png?raw=true)


Furthermore we add the following configurationfiles:
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
If we start the  **config-service** and request http://localhost:8888/employee-service/dev we get:
 ![Config Data from local repository for employee-service for dev profile](images/ConfigDataForEmployeeserviceDev.png?raw=true)
 

 If we request http://localhost:8888/employee-service/prod we get:  

 ![Config Data from local repository for employee-service for prod profile](images/ConfigDataForEmployeeserviceProd.png?raw=true)

Let's add the following property to the *employee-service.properties* and save the file :
```
message: Hello microservices world!
```
 If we request again  http://localhost:8888/employee-service/dev we get:


 ![Config Data from local repository for employee-service for dev profile after refresh](images/LiveRefreshOfConfigDataForEmployeeserviceDev.png?raw=true)
 
 To summarize here is a quotation from [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_client.html):
 > The Config Service serves property sources from `/{name}/{profile}/{label}`, where the default bindings in the client app are as follows:
>
>-   "name" = `${spring.application.name}`
>-   "profile" = `${spring.profiles.active}` 
>-   "label" = "master"

## The department-service and the employee-service
We need now to make our microservices pick up their respective configuration data from the **config-service**.
We will use exactly the same **department-service** and **employee-service** as in the [last tutorial](https://github.com/Meziano/tutorial-003).
### The configuration files
We must remove the configuration files of the 2 applications as the applications must now pick up their respective configuration data from the **config-service**.
To tell our microservices about the new situation we must make them aware about central configuration and to this end we add the following dependency to each of them:
```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-client</artifactId>
</dependency>
```  
It's also possible to use the starter `spring-cloud-starter-config` to configure an application as *config-client*.
### Let our services know about the config-service
To tell the **department-service** and **employee-service** about the **config-service** we have to add a *bootstrap.properties* file to both of them. 

> The properties in this file must be read in _before_ the rest of the application’s configuration is read from the Config Server, during the _bootstrap_ phase.

In this *bootstrap.properties* we say to the application, that it has to get its configuration data from the **config-service**:
```
spring.application.name=employee-service
spring.profiles.active=dev
spring.cloud.config.url=http://localhost:8888
```
When we start for example **employee-service** Spring notices the presence of a **bootstrap** file, so it starts a parent **application context** with the content of the **bootstrap** file and asks the **config-service** after the configuration data for the application to start. It must provide an *application name* and the *active profile*. In this example,  it says: "Hello Config Server at http://localhost:8888, I need the configuration data to start the application  '*employee-service*' for the **active profile** '*dev*'."

The **config-service** checks in the repository it manages if there is a configuration file with the name '*employee-service-**dev**.properties*' and it finds one, so it hands the file's content over. The **application context** requester starts now the **employee-service** with an *application context* based on the configuration data from the **config-service*.* 
(Note that we have until now used *.properties* files, but it's also possible and I find it better to use *.yml* file)

Requesting http://localhost:8082/employees will show us the list of the employees:

![find all employees using configuration data from config-service](images/findEmployeesByDepartmentIdUsingJavaClasses.png?raw=true)
It's important to have a look at the console:
```
Fetching config from server at : http://localhost:8888
Located environment: name=employee-service, profiles=[dev], label=null, version=null, state=null
Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='classpath:/config/employee-service-dev.properties'}]}
The following profiles are active: dev
Bootstrapping Spring Data repositories in DEFAULT mode.
Finished Spring Data repository scanning in 38ms. Found 1 repository interfaces.
BeanFactory id=101fc7cf-7026-3d45-ba7d-ebd00c85ab94
Bean 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration' of type [org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration$$EnhancerBySpringCGLIB$$9e56adac] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
Bean 'org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration' of type [org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration$$EnhancerBySpringCGLIB$$ba70b0a9] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
Tomcat initialized with port(s): 8082 (http)
Starting service [Tomcat]
Starting Servlet engine: [Apache Tomcat/9.0.17]
Initializing Spring embedded WebApplicationContext
Root WebApplicationContext: initialization completed in 979 ms
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```
The configuration properties are read once at the **employee-service**'s startup. To force the application to pull updated values from the *Config Server*, we just have to annotate the `EmployeeController` with the *Spring Cloud Config* `@RefreshScope` 
```
package de.meziane.ms.controller;
...
@RefreshScope
@RestController
public class EmployeeController {
  @Autowired
  EmployeeRepository employeeRepository;

  @Value("${message:Hello default}")
  private String message;

  @RequestMapping("/message")
  String getMessage() {
    return this.message;
  }
  ...
}
```
Please take note that a *getMessage()* method has been added to the `EmployeeController` to get the `message` from *config-service*. 

We must also enable the `/refresh` endpoint by adding:
```
management.endpoints.web.exposure.include=*
```
- either to the `employee-service-dev.properties` under `src/main/resources/config` on the **config-service** 
- or to a new `application.properties` under `src/main/resources` on the **employee-service**.
  
If we request http://localhost:8082/message we get:

![message ](images/employeeServiceMessage.png?raw=true)

Now if we:
1. change the value of message in the *employee-service-dev.properties* file to
```
message=Bonjour Monde des microservices! 
```
2. send a POST actuator/refresh with an empty JSON-object and 'Content-Type: application/json' in the header:
```
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8082/actuator/refresh --data '{}'
```
and request http://localhost:8082/message again, we get the updated message:

![live message update](images/employeeServiceMessageLiveUpdate.png?raw=true)

### Let the department-service and employee-service comunicate
In a last step, let's start the **department-service** with `bootstrap.properties` as the sole configuration file:
```
spring.application.name=department-service
spring.profiles.active=dev
spring.cloud.config.url=http://localhost:8888
``` 
We can see that the communication is still working.
 
### Summary
Our **department-service** and **employee-service** communicate as in the [last tuturial](https://github.com/Meziano/tutorial-003), but now they pull their respective configuration data  from the introduced **config-service** with an embedded **config server** that manages the configuration data in a file-system backend repository.  
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTEzMjI5NzczNDEsMTM3ODMxMTE3LDEwNz
Y2MTM1MjcsNDExNDk0OTU3LDEyODkyNzUwMjMsMTIyMjc4MzM3
NywtNjA5MzU2NjgxLC0zNDM0MDc3OTYsOTUwODE1NzMyLDM5MT
U1MDU1LDE2NDI0MzMzODEsLTE4NjAwODQ0NDEsMTYwMzczNjk2
Myw0NzU0Njk0MTksNzQwNDY2MTU2LC0xMzgyMjY1NTMzLC0xOT
M2NTc2ODksLTEwNDQ4MDg3OTYsLTE1NDk0MzMxMzEsODk4OTY5
MTQ4XX0=
-->