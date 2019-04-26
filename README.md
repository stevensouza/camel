# Apache Camel Sample Programs
experiments with apache camel and related technologies. Below is a brief description of the java  programs.

## Running programs
* To run the programs in any of the directories:  **mvn spring-boot:run**
* Note the apps include Hawt IO: http://localhost:8080/actuator/hawtio/
* Hawt IO has a Camel tab that has lots of useful information.  In particular the live route diagram is great for visualizing 
your routes and seeing how much data is passing through and whether or not errors are happening.
* Hawt io gives you a way to see JMX data and operations with http/webapp vs using JConsole.

![hawtio](https://github.com/stevensouza/camel/blob/master/images/hawtio.png)

## Docker info for running programs used in various experiments

* **activemq** - use the following command to run amq docker. activemq is used in experiment1
  * **docker run -p 61616:61616 -p 8161:8161 rmohr/activemq**
  * manage docker via web console - http://localhost:8161/
* **kafka** - running kafka.  Put local host ip in [kafka_experiment1.yml](https://github.com/stevensouza/camel/blob/master/docker/kafka_experiment1.yml) and run the following command (it will start up kafka and zookeeper. kafka will be on localhost:9092 as you can see in application.properties)
  * docker-compose -f kafka_experiment1.ymll up -d
* **mongodb** -  Run the following or start up mongodb in some other way on localhost and port 27017
  * **docker run --name mongo-xenial -d mongo:4.0-xenial**
  * Note i didn't need to expose the port though if for some reason the above doesn't work try: **docker run --name mongo-xenial -p 27017:27017 -d mongo:4.0-xenial**
  * To see data in mongodb
    * **docker run -it --link=mongo-xenial  mongo:4.0-xenial /bin/bash**
    * From Shell
      * To run program that allows for executing mongodb commands execute:
        * **mongo mongodb:27017**
      * Some mongodb commands
        * show dbs
        * use pojodb
        * db.mypojo.count();
        * db.mypojo.find();
  * [Usage in application.properties](https://github.com/stevensouza/cameldemo/blob/master/src/main/resources/application.properties)  
 * **apache drill** -  Apache drill allows you to write SQL select statements against data that doesn't natively support it such as mongodb (I use this in experiment4 below), kafka, s3, and file systems.
   * **docker run -i --name drill -p 8047:8047 -p 31010:31010 -t drill/apache-drill:1.15.0 /bin/bash  **
   * To enable writing mongodb queries do the following:
     * run mongodb (using docker above)
     * http://localhost:8047/storage
     * 'enable' mongodb
     * Get the mongodb docker ip address: docker inspect MONGO_CONTAINER_ID
       * docker inspect mongo-xenial 
     * 'update' the apache drill mongodb file via the url above, replacing localhost with the mongodb ipaddress, Example
        * {
          * "type": "mongo",
          * "connection": "mongodb://172.17.0.2:27017/",
          * "enabled": true
        * }
 * **aggregated logging** - Send logging from various machines to a central logging location such as papertrail or splunk. In some of these examples I send logs to the papertrail logging cloud. Simply sign up and get a destination URI for your acccount.  Papertrail allows you to query your centrally located logs in a manner similar to Splunk.  Alternatively a logback-spring.xml or logback.xml file can be configured to write to the centralized loggings service.  See experiment3_rest for an example of this approach.
   * When logging to papertrail you are logging to the cloud and data would be kept in the cloud and accessed at https://papertrailapp.com/dashboard.
   * You can start up one docker host that runs logspout.  It will take all docker.sock output on your machine and send all the data to your papertrail account. Here is the command:  
     * **docker run --restart=always -d -v=/var/run/docker.sock:/var/run/docker.sock gliderlabs/logspout   syslog+tls://logs7.papertrailapp.com:20749**  
     * And here is a command to start your spring boot app assuming you have used the mvn docker plugin per the below information.  Note --hostname is used as the name in papertrail to identify the log. If this isn't used the more docker cryptic name/number is used which makes identifying the log difficult.
       * **docker run --rm  --name camel_experiment4 --hostname camel_experiment4 stevesouza/camel_experiment4_mongo**

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
  * Simple Expression Language (sel) including ${properties} - ${properties:HISTFILESIZE} - https://github.com/stevensouza/camel/blob/a6edd639b20dce559dbc9698dc1918477ecdd155/experiment1_kafka_amq/src/main/java/com/stevesouza/camel/experiment1/MyCamelRoutes.java#L79
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
    
## [experiment2_testing](https://github.com/stevensouza/camel/tree/master/experiment2_testing)
  * Spring boot camel app that replaces camel endpoints (from/to) with 'direct' and 'mock' to test the routes.
  * Uses
    * Camel testing techniques
    * adviceWith - to alter routes in tests
    * mocks
    * ProducerTemplate and FluentProducerTemplate
    * A spring boot 'test' profile
    * activemq/jms endpoints
    * hawtio/jolokia
    
## [experiment3_rest](https://github.com/stevensouza/camel/tree/master/experiment3_rest) Spring boot app with rest endpoints. Automatically deploys to openshift via webhooks when committed, uses openshift ConfigMaps, centralized logging with PaperTrail from logback appender, correlationId's in log.
  * Spring boot camel app that uses rest endpoints. Some endpoints follow:
    * http://localhost:8080/rest/hi
    * http://localhost:8080/rest/hello
    * http://localhost:8080/rest/random - returns randomly populated person data
    * http://localhost:8080/rest/exception - shows exception and demonstrates camel exception handling
    * http://localhost:8080/rest/parallel/{myname} - takes an input and returns it with a message.  
    * http://localhost:8080/rest/swagger
    * http://localhost:8080/actuator - spring boot provided links that fall under /actuator like health, metrics, beans, env, info, heapdump, threaddump, httptrace (most recent http requests)
    * http://localhost:8080/actuator/hawtio/ - lots of great management info, diagrams, metrics for camel routes and more.
  * MyCamelRoutes Uses
    * rest() DSL
    * servlet component: from("servlet://hello")
    * xml/json generation 
    * routeId's
    * startupOrder
    * transform EIP
    * Accessing ${properties:PROP_NAME} from a camel route: https://github.com/stevensouza/camel/blob/4a487711589b7611646b37e536d613ebe221d400/experiment3_rest/src/main/java/com/stevesouza/camel/experiment3/MyCamelRoutes.java#L102
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
 * Note this project is configurred to deploy to openshift on a commit to the repo.
   * This application references MY_FIRST_NAME, and MY_LAST_NAME at http://localhost:8080/rest/hello.  They are defined in application.properties. However either may also be overridden in an openshift ConfigMap and associating the ConfigMap with this application in openshift.
   * Note spring-cloud-starter-kubernetes was added to the pom in order to have spring boot recognize ConfigMaps.
   * Centralized logging/log aggregation -  Note the best way to do centralized logging is to configure docker or openshift. In docker all containers on a host write to Docker.sock and this can be intercepted and written to a centralized logging service. My code uses an appender that writes to papertrail (papertrail is like splunk).  When logging to papertrail you are logging to the cloud (data would be accessed at https://papertrailapp.com/dashboard).
     * See [logback-spring.xml](https://github.com/stevensouza/camel/blob/master/experiment3_rest/src/main/resources/logback-spring.xml) for how the papertrail appender is configured.  Note logback configuration is only there to configure for papertrail use and if we don't have an appender it doesn't have to be there. You can also use appenders to write to other centralized loggers. 
     * using docker for centralized logging. Note in this case the appender need not be used:
        * docker run --restart=always -d    -v=/var/run/docker.sock:/var/run/docker.sock gliderlabs/logspout   syslog+tls://logs7.papertrailapp.com:20749
   * CorrelationId's are in the logs - This is accomnplished by simply adding spring-cloud-starter-sleuth to the pom.xml. Without any coding this will add applicaiton name,correlationId/traceId, and spanId to each log statement (see below for an example)
       * correlationId/traceId - is a unique id per request (same id even if multiple rest calls). It is used to track a request.
       * spanId - represents each service so even the spanId can change for each request (unlike the correlationId) as different microservices are called.
       * Example log statement: 2019-02-09 15:48:29.996  INFO [experiment3_rest,55d59f30f767ba4d,55d59f30f767ba4d,false] 13547 --- [nio-8080-exec-4] route.servlet.randomPerson               :  my log statement
* Also uses fabric8 docker maven plugin (https://dmp.fabric8.io/) (easily add your applicaiton to a docker image and run it - see below).  See experiment4 notes below for more details. 
  * **mvn install docker:build**
  * **docker run --rm --name camel_experiment3_rest --hostname camel_experiment3_rest -p 8080:8080 stevesouza/camel_experiment3_rest**
    * Note --rm removes the container when it is stopped.
    * --hostname allows papertrail to identify the log with this name as opposed to the more crytpic containerid


## [experiment4_mongo](https://github.com/stevensouza/camel/tree/master/experiment4_mongo). Note to run the program you must first start mongodb and apache drill using the docker commands for each mentioned above.
  * Spring boot camel app that writes pojos to mongodb, and reads the data with both camel native queries and apache drill SQL.
  * Docker
  * MongoDb3 component - save/insert pojo, count and retrieve objects in collection
    * See MyMongoCamelRoutes for usage
  * Apache Drill component - allows you to write ANSI SQL selects to access mongo collection data.  Although I didn't write them you can also write aggregate queries using 'group by'. 
    * See MyDrillCamelRoutes for usage  
  * hawtio
  * Timer component
  * Log eip
  * Properties loaded from application.properties
  * Random beans, and random beans validation (javax.validation)
    * There was a conflict between the versions of javax.validation in Random beans and drill.  Random beans used a more recent version, so I used a technique to exclude the version used by drill and explicitly include the dependency of the latter version (instead of depending on transitive dependencies).  See pom.xml 
  * lombok
  * fabric8 docker maven plugin (https://dmp.fabric8.io/) (easily add your applicaiton to a docker image and run it - see below)
  * Notes
    * When running from the localhost or IDE then application.properties host names for drill and mongo should be set to localhost
    * Building and running the app as a docker image using the fabric8 maven plugin (https://dmp.fabric8.io/)
      * When running the app from docker using fabric8 maven plugin put the drill and mongo ip address in application.properties.  This can be retrieved by 'docker inspect CONTAINER_ID' for each of them.
      * To build and deploy docker image for the 8 using maven fabric8 plugin: **mvn install docker:build**
      * To run the program (ensure mongo and drill are both running first): **docker run --rm --name camel_experiment4 stevesouza/camel_experiment4_mongo** 
      * To stop the program: **docker stop camel_experiment4**
      * Alternatively you can start/stop/tail logs of the application like this:
        *  **mvn install docker:start**
          * of if it is already installed: mvn docker:start
        * **docker logs --follow camel_experiment4_mongo-1**
          * or if you just want to display the log: **docker logs camel_experiment4_mongo-1**
        * **mvn docker:stop**

## [camel_experiment5_mongo_openshift](https://github.com/stevensouza/camel/tree/master/camel_experiment5_mongo_openshift). This is the same codebase as experiment4 above except it was changed to work within openshift/kubernetes in the following ways.  Uses auto deployment to openshift, ConfigMaps, Secrets (for mongodob), correlationId's in logs.
* See experiment4 notes for more about what the app does and how it works as experiment5 was mostly a copy of its code.
* application.properties was changed to work with openshift ConfigMaps and Secrets (env variables).  For example the following property was added.  The variables can be defined in either application.properties or in the case of openshift - ConfigMaps and Secrets can be defined to override the values in application.properties:
MONGODB_URI=mongodb://${MONGODB_HOST}:${MONGODB_PORT}/${MONGODB_DBNAME}
* Mongodb is started up in openshift. Note it's 'service' name and so its DNS is: mongodb
* A webhook was added to the github repo so that every time the code is commited openshift does a rebuild/redeploy
* Apache Drill was used in experiment4, but removed from the experiment5 code to simplify the app.  As the intention is to get the code to work in openshift and so a simpler example works better.
* Note there is a fabric8 maven plugin that deploys directly to openshift/kubernetes.  I didn't use this and did a standard docker plugin. 
* Has correlationId added automatically to logging via spring-cloud-starter-sleuth in pom.xml.  Note becauset this app doesn't have rest calls this capability isn't demonstrated.  Just wanted this standard microservices capability in one of my latest pom.xml files.

## [camel_experiment6_soap](https://github.com/stevensouza/camel/tree/master/experiment6_soap). Uses spring boot rest controller to call a publicly available soap service via the camel cxf component
* Spring boot rest
* hawtio - view metrics, view camel routes,...
* swagger
* camel cxf - used to call soap/wsdl
* Makes a call to a camel jmx bean to display Metrics data for both automon and camel. Note spring uses micrometer now so spring metrics can be found here: http://localhost:8080/actuator/metrics. Automon will put metrics in here if automon.properties specifies micrometer as the underlying monitoring library.
  * To specify an individual metric: http://localhost:8080/actuator/metrics/execution(int org.tempuri.AddResponse.getAddResult())
* Uses 'MySpringAspect mySpringAspect = Aspects.aspectOf(MySpringAspect.class)' to grab automon singleton aspect and configure
* Uses [automon](https://github.com/stevensouza/automon) to monitor "com.stevesouza..*" and "org.tempuri".  (See MySpringAspect for details).  
  * automon.properties was added and the monitor defined in there is jamon.  You can also change the monitor by going into jmx console or hawtio and typing sysout, jamon, metrics... (assuming they are in the classpath) for the OpenMon property. This will instantiate the proper object (see AutoMon's AutomonMXBean interface and AutomonJmx implementation)
  * Note the project is compiled with AspectJ's Build Time Weaving (BTW) for a Spring boot/camel app.  This means that only classes in the project (i.e. source code) can be monitored.  So for example jdbc can't be.  To monitor jdbc and other java classes or 3rd party libraries you would have to use LTW (Load time weaving).  An advantage of using aspectj over Spring's built in monitoring is that Spring only allows you to monitor Spring beans whereas aspectj lets you monitor anything (Pojo's/utility classes etc).
  * Note aspectj seems to be incompatible with lombok.  In this project I didn't use it so that is acceptable, but a better solution would be nice.
  * Also it would be good to see how to use LTW with a spring boot app as that is more powerful.
  * This application uses jamon and so there is a JamonController that displays the jamon html report.  The report can be sorted by the column index (starting at 0 for the first column).  sortOrder can be asc/desc. Examples follow...
    * default descending on hits column - http://localhost:8080/jamon
      * defaults are colIndex=2 (hits) and desc/descendiing.
    * descending on average - http://localhost:8080/jamon?colIndex=3&sortOrder=desc
    * descending on average - http://localhost:8080/jamon?colIndex=3
    * descending on total - http://localhost:8080/jamon?colIndex=4
    * descending on max - http://localhost:8080/jamon?colIndex=8
    * Sample automon/jamon report sorted descending on hits: ![automon report](https://github.com/stevensouza/camel/blob/master/images/experiment6_jamon_report.png)
  * You can also change the automon.properties value to use metrics. If you do both automon and camel metrics are kept in the same metrics registry and can be viewed with
    * http://localhost:8080/metrics

     


## [cameldemo](https://github.com/stevensouza/cameldemo)
  * uses camel, jms/activemq, mongodb (older version of docker component i.e. mongodb and not mongodb3), docker

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
  
