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
    @EndpointInject(uri = "direct:generateRandomData.controlbus")
    FluentProducerTemplate controlBus;

    @GetMapping("/start")
    public String start() {
        // request() expects a return falue and send() does not.
        // action could be either start or resume
        log.info("starting/resuming route");
        // note in this case the route returns info to the browser, though only doing it this way ot show how it is done.
        // probably best to just do a plain 'send' and have the controller itself return the message.
        return controlBus.withHeader("action", "resume").request(String.class);
    }

    @GetMapping("/stop")
    public String stop() {
        // action could either be stop or suspend
        log.info("stopping/suspending route");
        return controlBus.withHeader("action", "suspend").request(String.class);
    }

}
