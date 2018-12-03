package com.stevesouza.camel.experiment1;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
    private PersonName personName;
    private String personDescriptionText;
    private int age;
}
