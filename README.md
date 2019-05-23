# Microservices Tutorial-004
## Let our microservices be independents
### Introduction
In the last [tuturial-003](https://github.com/Meziano/tutorial-003) we have hardcoded the URL of the **employee-service** in the **department-service** and this is not very wise: What if the used port changes? What if we have 10 or more microservices that need to communicate with each other and that must have at least differents server-port on different environments like development, test and production?
It's abvious that it makes no sense it make sense to have to manually update each and every one of these services. 
The idea is to make uses the Config Server to load its own configuration and that _refreshes_ its configuration to reflect changes to the Config Server on-demand, without restarting the JVM

> provide server and client-side support for externalized configuration
> in a distributed system. With the Config Server you have a central
> place to manage external properties for applications across all
> environments.”

### Summary
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE5OTY1NTMzNzQsMTg4NzA2MzQwLDE5Mj
AxMTUyNTZdfQ==
-->