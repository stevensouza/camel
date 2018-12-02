package com.stevesouza.camel.experiment1;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * todo
     * look at methods for fluenproducer and routes.
     * kafka, amq, randombeans, modelmapper
     * mimic irsingester logic
     * different components
     * * test
     * * readme file
     * * image of hawtio
     * * review code at camelinaction and on website
     * http://localhost:8080/actuator/hawtio/
     * look at hawt io
     */

    @Override
    public void configure() throws Exception {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        // always assign a routeId for easy route identification
        from("direct:start")
                .routeId("start")
                .transform(constant("i am starting (from camel)"));

        from("direct:stop")
                .routeId("stop")
                .transform(constant("i am stopping (from camel)"));
        //   .bean(MyBean.class);


    }

    public static class MyBean {
        public String start() {
            return "I started";
        }
    }

}