package com.stevesouza.xml;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
    private PersonName PersonName;

    private String PersonDescriptionText;

    private String PersonHandednessText;
}
