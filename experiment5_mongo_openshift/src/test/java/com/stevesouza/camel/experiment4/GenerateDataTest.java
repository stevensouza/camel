package com.stevesouza.camel.experiment4;

import com.stevesouza.camel.experiment4.data.Person;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateDataTest {

    @Test
    public void createPerson() {
        for (int i=0;i<100;i++) {
            Person person = GenerateData.createPerson();
            assertThat(person.getAge()).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(120);
        }
    }
}