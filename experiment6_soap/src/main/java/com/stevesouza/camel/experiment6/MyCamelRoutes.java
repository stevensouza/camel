package com.stevesouza.camel.experiment6;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    // externalized information for accessing math web service
    @Value("${camel.route.math.from}")
    private String readFromWebService;

    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** Defined routes:
     *      - route.math" - Called from rest controller to perform math by calling cxf soap webservices
     *          component which in turn calls a publicly available rest endpoint that either adds, subtracts,
     *          multiplies or divides 2 integers
     *      - to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     *
     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        //
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        from("direct:math2")
                .toD(readFromWebService);
        
        from("direct:math")
                .routeId("route.math")
                .log("${header.operationName}: ${body}")
                .toD(readFromWebService)
                .setBody(simple("The ${header.operationName} response was ${body}"))
                .log("${body}");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}