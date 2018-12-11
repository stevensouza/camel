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

    // Route from/to information has been externalized into application.properties
    @Value("${camel.route.generateRandomData.from}")
    private String generateRandomDataFrom;

    @Value("${camel.route.processData.activemq.to}")
    private String writeToAmq;

    @Value("${camel.route.readFromActiveMq.from}")
    private String readFromAmq;


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** Defined routes:
     *      - route.generateRandomData.controlbus - Called from rest controller to start/stop the main route
     *          (route.generateAndWriteRandomDataToAmq")
     *      - route.generateAndWriteRandomDataToAmq" - generate random data and write it to amq
     *      - route.readFromActiveMq - read the data generated above from amq
     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        // Control main route - start/stop data generation via rest
        from("direct:generateRandomData.controlbus")
                // note routeId's are helpful in the following ways: they write the name to the logs
                // , make routes readable in hawtio console, allow you to grab and manipulate (for example adding advice)
                // the route easily in tests.
               .routeId("route.generateRandomData.controlbus")
               .log("action=${header.action}")
               // controlbus is an eip that allows you to start/stop/suspend/resume routes among other things.
               .toD("controlbus:route?routeId=route.generateAndWriteRandomDataToAmq&action=${header.action}")
                // you get this data back with reply request
               .transform(simple("Action taken on route: ${header.action}"));

        // Generate random Person data and write to amq
        from(generateRandomDataFrom)
                .routeId("route.generateAndWriteRandomDataToAmq")
                .noAutoStartup() // don't run route on app startup. The control bus below will start/stop it on demand
                // The methods return value will be used to generate a random Person object and populate setBody(..) with it
                .bean(GenerateData.class)
                .log("Generated & writing random pojo data to amq. Data=${body}")
                .marshal().json(JsonLibrary.Jackson)
                 //  '#jms' is used to look up the spring bean of that name to get the connectionFactory from
                .toD(writeToAmq).id("amqdest"); // can use the id in tests with advice: weaveById("amqdest").replace() (remove() etc.)


        // read data that was written to amq
        from(readFromAmq)
                .routeId("route.readFromActiveMq")
                .log("Retrieved data from kafka topic: ${body}");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}