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
  * docker run --name mongo-xenial -d mongo:4.0-xenial
  * Note i didn't need to expose the port though if for some reason the above doesn't work try: docker run --name mongo-xenial -p 27017:27017 -d mongo:4.0-xenial
  * To see data in mongodb
    * docker run -it --link=mongo-xenial  mongo:4.0-xenial /bin/bash
    * From Shell
      * To run program that allows for executing mongodb commands execute:
        * mongo mongodb:27017
      * Some mongodb commands
        * show dbs
        * use pojodb
        * db.mypojo.count();
        * db.mypojo.find();
  * [Usage in application.properties](https://github.com/stevensouza/cameldemo/blob/master/src/main/resources/application.properties)  
 * **apache drill** -  Apache drill allows you to write SQL select statements against data that doesn't natively support it such as mongodb (I use this in experiment4 below), kafka, s3, and file systems.
   * docker run -i --name drill -p 8047:8047 -p 31010:31010 -t drill/apache-drill:1.15.0 /bin/bash  
   * To enable writing mongodb queries do the following:
     * run mongodb (using docker above)
     * http://localhost:8047/storage
     * 'enable' mongodb
     *  Get the mongodb docker ip address: docker inspect MONGO_CONTAINER_ID
     * 'update' the apache drill mongodb file via the url above, replacing localhost with the mongodb ipaddress, Example
        * {
          * "type": "mongo",
          * "connection": "mongodb://172.17.0.2:27017/",
          * "enabled": true
        * }

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
  * MyCamelRoutes Uses
    * rest() DSL
    * servlet component: from("servlet://hello")
    * xml/json generation 
    * routeId's
    * startupOrder
    * transform EIP
    * swagger api and swagger annotations and route metadata
    * log component and log EIP
    * Calling a bean from a route
    * onExeption for exception handling and throwing an exception from a route
    * Disable intellij formatting for routes (in routeBuilder.configure())
      *  // @formatter:off
      *   public void configure() {
      *    ...
      *   }
      *  // @formatter:on
    * Various properties set in application.properties
    * Setting a camel header (setHeader) and setting the body (setBody)
    * lombok
    * hawtio/jolokia
  * MyParallelCamelRoutes in addition to some of the above uses
    * rest() DSL 
    * seda component: to("seda:single_queue",...), from("seda:merge_queue")
    * multiple destinations (to("seda:single_queue", "seda:parallel_queue")).  Note you can also use multiple consumers in a from  (i.e. from(uri1, uri2,...)
    * EIPs: transform, multicast, log
    * parallelProcessing which is possible on some EIPs (split, multicast for example) as well as the 'seda' consumer as an option.
      * from("seda:parallel_queue?concurrentConsumers=20")

## [experiment4_mongo](https://github.com/stevensouza/experiment4_mongo). Note to run the program you must first start mongodb and apache drill using the docker commands for each mentioned above.
  * Spring boot
  * Docker
  * MongoDb3 component - save/insert pojo, count and retrieve objects in collection
  * Apache Drill component - allows you to write ANSI SQL selects to access mongo collection data.  Although I didn't write them you can also write aggregate queries using 'group by'. 
  * hawtio
  * Timer component
  * Log eip
  * Properties loaded from application.properties
  
## [cameldemo](https://github.com/stevensouza/cameldemo)
  * uses camel, jms/activemq, mongodb, docker

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
  
