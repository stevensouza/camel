package com.stevesouza.camel.experiment1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
public class CamelXmlApplication {


    /**
     * to view visual representations of camel routes and to control jmx via http
     * http://localhost:8080/actuator/hawtio/
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(CamelXmlApplication.class, args);
    }

}
