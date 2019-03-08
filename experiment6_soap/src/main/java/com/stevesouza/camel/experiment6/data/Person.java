package com.stevesouza.camel.experiment6.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
    private PersonName personName;
    private String personDescriptionText;
    private int age;
}
