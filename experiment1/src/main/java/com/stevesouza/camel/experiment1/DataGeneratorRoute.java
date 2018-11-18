package com.stevesouza.camel.experiment1;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DataGeneratorRoute extends SpringRouteBuilder {


    @Override
    public void configure() throws Exception {
        // An xml classpath File is passed in as input (although it could also be a String xml document too)
        from("direct:tojson1")
                .routeId("tojson1")
                .log("xml to add field to and convert to json: ${body}")
                .unmarshal().jacksonxml() // convert from xml to a Map
                .process(new MyProcessor()) // add a field to the map
                .marshal().jacksonxml() // convert map back to xml
                .log("converted ${body}");


        // An xml classpath File is passed in as input (although it could also be a String xml document too)
        from("direct:tojson2")
                .routeId("tojson2")
                .log("xml to convert to json;  ${body}")
                .marshal().xmljson() // convert from xml to json directly (as opposed to using a intermediate object as in above)
                .log("converted ${body}");

        // An xml classpath File is passed in as input (although it could also be a String xml document too)
        from("direct:displaytype")
                .routeId("displaytype")
                .log("input: ${body}")
                .convertBodyTo(String.class)
                .process(new PrintClassProcessor());

        // An xml classpath File is passed in as input (although it could also be a String xml document too)
        from("direct:validatexml")
                .routeId("validatexml")
                .log("xml to validate: ${body}")
                .delayer(5000)
                .bean(XSDValidatorBean.class)
                .log("XML content was validated");
    }
}