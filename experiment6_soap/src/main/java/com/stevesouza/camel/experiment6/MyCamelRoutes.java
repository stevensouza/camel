package com.stevesouza.camel.experiment6;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.automon.implementations.Metrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

//    @Autowired
//    private MySpringAspect automonAspect;

    // externalized information for accessing math web service
    @Value("${camel.route.math.from}")
    private String webServiceRoute;

    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes

    /**
     * Defined routes:
     * - route.math" - Called from rest controller to perform math by calling cxf soap webservices
     * component which in turn calls a publicly available rest endpoint that either adds, subtracts,
     * multiplies or divides 2 integers
     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        // to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
        getContext().addRoutePolicyFactory(getMetricsRoutePolicyFactory());

        from("direct:math2")
                .toD(webServiceRoute);

        from("direct:math")
                .routeId("route.math")
                .log("${header.operationName}: ${body}")
                .toD(webServiceRoute)
                .setBody(simple("The ${header.operationName} response was ${body}"))
                .log("${body}");

    }

    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes

    private MetricsRoutePolicyFactory getMetricsRoutePolicyFactory() {
        MetricsRoutePolicyFactory factory = new MetricsRoutePolicyFactory();
        // get aspect singleton and ensure that camel and the aspect use the same metrics registry
        Metrics metricsMon = MetricsController.getMetrics();
        if (metricsMon != null) {
            factory.setMetricsRegistry(metricsMon.getMetricRegistry());
        }

        return factory;
    }
}