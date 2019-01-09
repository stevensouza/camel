package com.stevesouza.camel.experiment4;

import com.stevesouza.camel.experiment4.data.Person;
import io.github.benas.randombeans.api.EnhancedRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenerateData {

    // The annotation lets camel know to use this method.  Currently as this class only has one method it doesn't really need it though
    // Also, note that the method can have 0 or more parameters.  By convention the first argument would be the body if it is used.
    // I am putting Exchange in their just to demonstrate, though in this case it isn't needed.
    @Handler
    public Person getRandomPerson(Exchange exchange) {
        return createPerson();
    }

    public static Person createPerson() {
        Person person = EnhancedRandom.random(Person.class);
        log.info("Generating random data: " + person);
        return person;
    }
}
