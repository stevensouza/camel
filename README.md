# camel
experiments with apache camel and related technologies

## Running programs
* To run the programs in any of the directories:  mvn spring-boot:run
* Note the apps include Hawt IO: http://localhost:8080/actuator/hawtio/
* Hawt IO has a Camel tab that has lots of useful information.  In particular the live route diagram is great for visualizing 
your routes and seeing how much data is passing through and whether or not errors are happening.
* Hawt io gives you a way to see JMX data and operations with http/webapp vs using JConsole.


## experiment1 - experiments using spring boot rest endpoints to send messages to camel routes. The endpoints are

http://127.0.0.1:8080/experiment1/start
http://127.0.0.1:8080/experiment1/stop
