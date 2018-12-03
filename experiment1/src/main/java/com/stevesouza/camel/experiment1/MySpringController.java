package com.stevesouza.camel.experiment1;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/experiment1")
@Slf4j
public class MySpringController {
    @Value("${experiment1.broker_type}")
    private String brokerType;

    // note you could reuse the same FluentProducerTemplate and set the endpoint (probably template.to("direct:start")
    @EndpointInject(uri = "direct:start")
    FluentProducerTemplate start;

    @EndpointInject(uri = "direct:stop")
    FluentProducerTemplate stop;

    @GetMapping("/start")
    public String start() {
        // request() expects a return falue and send() does not.
        return start.withHeader("broker_type", brokerType).request().toString();
    }

    @GetMapping("/stop")
    public String stop() {
        return stop.request(String.class);
    }

}
