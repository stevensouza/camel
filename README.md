# Apache Camel Sample Programs
experiments with apache camel and related technologies. Below is a brief description of the java programs.

## Running programs
* To run the programs in any of the directories:  mvn spring-boot:run
* Note the apps include Hawt IO: http://localhost:8080/actuator/hawtio/
* Hawt IO has a Camel tab that has lots of useful information.  In particular the live route diagram is great for visualizing 
your routes and seeing how much data is passing through and whether or not errors are happening.
* Hawt io gives you a way to see JMX data and operations with http/webapp vs using JConsole.

![hawtio](https://github.com/stevensouza/camel/blob/master/images/hawtio.png)

## Docker info for running programs used in various experiments

* **activemq** - use the following command to run amq docker. activemq is used in experiment1
  * docker run -p 61616:61616 -p 8161:8161 rmohr/activemq
  * manage docker via web console - http://localhost:8161/
* **kafka** - running kafka.  Put local host ip in [kafka_experiment1.yml](https://github.com/stevensouza/camel/blob/master/docker/kafka_experiment1.yml) and run the following command (it will start up kafka and zookeeper. kafka will be on localhost:9092 as you can see in application.properties)
  * docker-compose -f kafka_experiment1.ymll up -d
* **mongodb** -  Run the following or start up mongodb in some other way on localhost and port 27017
  * docker run --name mongo-camel -p 27017:27017  -d mongo
  * To see data in mongodb
    * docker run -it --link=mongo-camel  mongo /bin/bash
    * From Shell
      * To run program that allows for executing mongodb commands execute:
        * mongo mongodb:27017
      * Some mongodb commands
        * show dbs
        * use pojodb
        * db.mypojo.count();
        * db.mypojo.find();
  * [Usage in application.properties](https://github.com/stevensouza/cameldemo/blob/master/src/main/resources/application.properties)    

## [experiment1_kafka_amq](https://github.com/stevensouza/camel/tree/master/experiment1_kafka_amq) (click to go to the projects source code)

This code requires running Kafka and ActiveMq (see above for instructions for running in docker)

* Technologies used
  * Kafka - read/write from topics
  * JMS/Activemq - read/write VirtualTopic
  * Spring boot
  * Spring rest/controller
  * Docker
  * Camel
  * Random beans
  * Lombok
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
    
## [experiment2_testing](https://github.com/stevensouza/experiment2_testing)
  * Spring boot camel app that replaces camel endpoints (from/to) with 'direct' and 'mock' to test the routes.
  * Uses
    * Camel testing techniques
    * adviceWith - to alter routes in tests
    * mocks
    * ProducerTemplate and FluentProducerTemplate
    * A spring boot 'test' profile
    * activemq/jms endpoints
    * hawtio/jolokia
    
## [experiment3_rest](https://github.com/stevensouza/experiment3_rest)
  * Spring boot camel app that uses rest endpoints.
  * Uses
    * rest() DSL
    * xml/json generation 
    * routeId's
    * startupOrder
    * transform EIP
    * swagger api and swagger annotations and route metadata
    * log component and log EIP
    * Calling a bean from a route
    * onExeption for exception handling and throwing an exception from a route
    * Disable intellij formatting for routes
    * Various properties set in application.properties
    * Setting a camel header (setHeader) and setting the body (setBody)
    * lombok
    * hawtio/jolokia
    
## [cameldemo](https://github.com/stevensouza/cameldemo)
  * uses camel, jms/activemq, mongo, docker

## [xmlxsd](https://github.com/stevensouza/camel/tree/master/xmlxsd) 
  * xml validation with xsd
  * niem documents
  * xml to json conversion
  * camel
  
## camel from testproject playground
  * [Code](https://github.com/stevensouza/testproject/tree/master/playground/src/main/java/com/stevesouza/camel)
     * EventNotifierSupport
     * Component: File
     * Camel Annotations: @Header, @Body, @Simple
     * json marshal/unmarshal using xstream and jackson
     * Processor
  * [Tests](https://github.com/stevensouza/testproject/tree/master/playground/src/test/java/com/stevesouza/camel)
     * assertions
     * Components: direct, seda, stream, mock
     * EIPs: filter (on header), multicast
     * intercept, interceptFrom, interceptSendToEndpoint
     * Processor
     * json marshal/unmarshal
  
