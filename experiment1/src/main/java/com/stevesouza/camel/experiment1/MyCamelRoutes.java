package com.stevesouza.camel.experiment1;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * todo
     * look at methods for fluentproducer and routes.
     * start/stop routes api
     * choice (header) kafka, amq, randombeans, modelmapper
     * mimic irsingester logic
     * review log
     * commit from intellij - sonar
     * check actuator endpoints
     * different components
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
        from("direct:start")
                .routeId("start")
                .choice()
                    // getting property for spring application.properties file
                    .when(simple("${properties:experiment1.broker_type} == 'kafka'"))
                        .log("broker_type is kafka")
                    .otherwise()
                        .log("broker type is activemq")
                .end()
                .transform(constant("i am starting (from camel)"));

        from("direct:stop")
                .routeId("stop")
                .transform(constant("i am stopping (from camel)"));
        //   .bean(MyBean.class);


    }

    // @formatter:on - enable intellij's reformat command from messing up indentation in camel routes


    public static class MyBean {
        public String start() {
            return "I started";
        }
    }

}