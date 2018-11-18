package com.stevesouza.camel.experiment1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class CamelXmlApplication {


    /**
     * to view visual representations of camel routes and to control jmx via http
     * http://localhost:8080/actuator/hawtio/
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(CamelXmlApplication.class, args);
        DataGenerator generator = context.getBean("dataGenerator", DataGenerator.class);
//        generator.send("hello world");
        for (int i=0; i<100000; i++) {
            generator.send("direct:tojson1", new ClassPathResource("person.xml").getFile());
            generator.send("direct:tojson2", new ClassPathResource("person.xml").getFile());
            generator.send("direct:displaytype", new ClassPathResource("person.xml").getFile());
            generator.send("direct:validatexml", new ClassPathResource("person.xml").getFile());
            Thread.sleep(1000);
        }

    }

}
