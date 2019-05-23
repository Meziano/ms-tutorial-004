# Microservices Tutorial-004
## Let our microservices be independents
### Introduction
In the last [tuturial-003](https://github.com/Meziano/tutorial-003) we have hardcoded the URL of the **employee-service** in the **department-service** and this is not very wise: What if the used port changes? What if we have 10 or more microservices that need to communicate with each other and that have at lest  differents po? 
The idea is to make uses the Config Server to load its own configuration and that _refreshes_ its configuration to reflect changes to the Config Server on-demand, without restarting the JVM

### Summary
<!--stackedit_data:
eyJoaXN0b3J5IjpbNjIzMzMzNDAsMTg4NzA2MzQwLDE5MjAxMT
UyNTZdfQ==
-->