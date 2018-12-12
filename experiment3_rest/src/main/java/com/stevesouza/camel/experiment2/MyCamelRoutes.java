package com.stevesouza.camel.experiment2;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * camel kafka component documentaiton:
     * https://github.com/apache/camel/blob/master/components/camel-kafka/src/main/docs/kafka-component.adoc
     * <p>
     * to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     * activemq - http://localhost:8161/
     */


    @Value("${camel.route.http.port}")
    private int port;


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** Defined routes:

     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        from("jetty://http://0.0.0.0:{{camel.route.http.port}}/hello")
                .routeId("route.helloWorld")
                .transform().simple("hello world");

        restConfiguration()
                .contextPath("rest")
                .port(port);

        // returns person.toString();
        rest().produces("text/plain")
                .get("/random").to("bean:generateData?method=getRandomPerson");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}