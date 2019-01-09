package com.stevesouza.camel.experiment4;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CamelXmlApplication {
    @Value("${spring.data.mongodb.host}")
    private String mongoServerUrl;

    @Value("${spring.data.mongodb.port}")
    private int mongoServerPort;

    /**
     * to view visual representations of camel routes and to control jmx via http
     * http://localhost:8080/actuator/hawtio/
     *
     * @param args
     */
    public static void main(String[] args) {
        createContext(args);
    }

    public static ConfigurableApplicationContext createContext(String[] args) {
        return SpringApplication.run(CamelXmlApplication.class, args);
    }

    @Bean("mongoClientConnectionBean")
    public MongoClient mongo() {
        return new MongoClient(mongoServerUrl, mongoServerPort);
    }


}
