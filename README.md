# camel
experiments with apache camel and related technologies. Below is a brief explanation of the programs.

## Running programs
* To run the programs in any of the directories:  mvn spring-boot:run
* Note the apps include Hawt IO: http://localhost:8080/actuator/hawtio/
* Hawt IO has a Camel tab that has lots of useful information.  In particular the live route diagram is great for visualizing 
your routes and seeing how much data is passing through and whether or not errors are happening.
* Hawt io gives you a way to see JMX data and operations with http/webapp vs using JConsole.

![hawtio](https://github.com/stevensouza/camel/blob/master/images/hawtio.png)

## [experiment1](https://github.com/stevensouza/camel/tree/master/experiment1) 

* Technologies used
  * Kafka - read/write from topics
  * JMS/Activemq - read/write VirtualTopic
  * Spring boot
  * Spring rest
  * Docker
  * Camel
* Highlights
  * Components used
    *  schedule - Used 'schedule' component to generate data to send to kafka/amq
    *  activemq
    *  kafka
  * EIPs used
    * choice - (if-then-else) to use a property to decide if data should be sent to kafka or amq
    * transform
  * Simple Expression Language (sel) including ${properties}
  * Routes from/to loaded from properties file
  * Using hawtio - great for viewing jmx and graphically viewing camel routes
    * http://localhost:8080/actuator/hawtio/
  * Added dropwizard metrics display to camel
  * Naming routes (routeId)
  * Calling a bean from a route
  * Setting camel headers
  * Converting POJO to/from JSON (i.e. marshaling/unmarshaling)
  * Sending 'direct' messages
  * Random beans library to generate random data
  * Enable spring actuator endpoints (health, metrics,...)
  * Running kafka and activemq via Docker
  * Jms/activemq configuration using Spring
  * Spring rest controller to start/stop a camel route using the controlbus EIP. Rest endpoints follow
    * http://127.0.0.1:8080/experiment1/start
    * http://127.0.0.1:8080/experiment1/stop
