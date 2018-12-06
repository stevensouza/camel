# Docker info for running programs used in various experiments

* activemq - use the following command to run amq docker. activemq is used in experiment1
  * docker run -p 61616:61616 -p 8161:8161 rmohr/activemq
  * manage docker via web console - http://localhost:8161/
* kafka - running kafka.  Put local host ip in kafka_experiment1.yml and run the following command (it will start up kafka and zookeeper. kafka will be on localhost:9092 as you can see in application.properties)
  * docker-compose -f kafka_experiment1.ymll up -d
