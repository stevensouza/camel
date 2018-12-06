# camel
experiments with apache camel and related technologies

## Running programs
* To run the programs in any of the directories:  mvn spring-boot:run
* Note the apps include Hawt IO: http://localhost:8080/actuator/hawtio/
* Hawt IO has a Camel tab that has lots of useful information.  In particular the live route diagram is great for visualizing 
your routes and seeing how much data is passing through and whether or not errors are happening.
* Hawt io gives you a way to see JMX data and operations with http/webapp vs using JConsole.


## experiment1 

* Highlights and Technologies used
  * Kafka - read/write from topics
  * Activemq - read/write VirtualTopic
  * Simple Expression Language (sel) including ${properties}
  * Choice EIP (if-then-else) to use a property to decide if data should be sent to kafka or amq
  * Routes from/to loaded from properties file
  * Using hawtio - great for viewing jmx and graphically viewing camel routes
    * http://localhost:8080/actuator/hawtio/
  * Added dropwizard metrics display to camel
  * Naming routes
  * Calling a bean from a route
  * Setting camel headers
  * Converting POJO to/from JSON (i.e. marshaling/unmarshaling)
  * Sending 'direct' messages
  * Using the 'schedule' component to generate data to send to kafka/amq
  * Enable spring actuator endpoints (health, metrics,...)
  * Running kafka and activemq via Docker
  * Jms/activemq configuration using Spring
  * Spring boot
  * Spring rest controller to start/stop a camel route using the controlbus EIP. Rest endpoints follow
    * http://127.0.0.1:8080/experiment1/start
    * http://127.0.0.1:8080/experiment1/stop
