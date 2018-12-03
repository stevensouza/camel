package com.stevesouza.camel.experiment1;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * look at methods for fluentproducer and routes.
     * start/stop routes api
     * choice (header) kafka, amq, randombeans, modelmapper
     * mimic irsingester logic
     * template.sendBody("controlbus:route?routeId=foo&action=start", null);
     * review log
     * commit from intellij - sonar
     * check actuator endpoints
     * different components
     * mongo
     * * test
     * * readme file
     * * image of hawtio
     * * review code at camelinaction and on website
     * http://localhost:8080/actuator/hawtio/
     * look at hawt io
     */

    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    @Override
    public void configure() throws Exception {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        // always assign a routeId for easy route identification
        //.noAutoStartup()
        // You can set a node description with: .description("my description")
        from("direct:processData")
              .routeId("route.processData")// set the route name and description which will display in hawtio
              .routeDescription("Route that either sends message to kafka, or amq based on config property")
              .log("Processing data=${body}")
                .choice()
                    .id("Choice: kafka or amq?")// the name for this 'choice' node which will show up in hawtio route diagram
                    // getting property for spring application.properties file
                    .when(simple("${properties:experiment1.broker_type} == 'kafka'"))
                        .log("broker_type is kafka")
                    .otherwise()
                        .log("broker type is activemq")
                .end();

        // also could use timer or quartz
        from("scheduler://generate_data?delay=5s")
                .routeId("route.generateRandomData")
                .noAutoStartup() // don't run route on app startup. The control bus below will start/stop it on demand
                // The methods return value will be used to setBody(..)
                .bean(GenerateData.class)
                .log("Generated random data=${body}")
                .to("direct:processData");

        from("direct:generateRandomData.controlbus")
                .routeId("route.generateRandomData.controlbus")
                .log("action=${header.action}")
                // controlbus is an eip that allows you to start/stop/suspend/resume routes among other things.
                .toD("controlbus:route?routeId=route.generateRandomData&action=${header.action}")
                .transform(simple("Action taken on route: ${header.action}"));
    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}