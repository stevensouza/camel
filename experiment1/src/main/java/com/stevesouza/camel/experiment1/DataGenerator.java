package com.stevesouza.camel.experiment1;

import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator {
    @Autowired
    CamelContext context;
    FluentProducerTemplate template;

    public void send(String uriDirectEndpoint, Object body) {
        if (template==null) {
            template = context.createFluentProducerTemplate();
        }
        template.setDefaultEndpointUri(uriDirectEndpoint);
        template.withBody(body);
        template.send();
    }

}