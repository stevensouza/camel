package com.stevesouza.camel.experiment3.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
// the following is needed for jaxb to render as xml though jackson doesn't need this annotation for json
@XmlRootElement
public class Person {
    // The following annotation documents the model object for swagger.
    @ApiModelProperty(value = "The persons name", required = true)
    private PersonName personName;
    @ApiModelProperty(value = "Describe the person", example = "This is Joe. He is a nice guy with brown hair.")
    private String personDescriptionText;
    @ApiModelProperty(value = "The age of the person")
    private int age;
}
