package com.stevesouza.xml;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class MyProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        System.err.println(exchange.getIn().getBody().getClass());
        System.err.println(exchange.getIn().getBody());
        Map map = (Map) exchange.getIn().getBody();
        map.put("mynewkey", "my new value");
        System.err.println(map);
    }
}
