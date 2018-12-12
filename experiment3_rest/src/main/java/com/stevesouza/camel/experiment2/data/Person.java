package com.stevesouza.camel.experiment2.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
// the following is needed for jaxb to render as xml though jackson doesn't need this annotation for json
@XmlRootElement
public class Person {
    private PersonName personName;
    private String personDescriptionText;
    private int age;
}
