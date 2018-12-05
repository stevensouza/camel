package com.stevesouza.camel.experiment1;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * activemq
     * read https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#jms
     * VirtualTopic.
     * pub.sub=true
     * multiple activemq hosts
     * auto jms data coversion without calling marshall
     * <p>
     * * look at methods for fluentproducer and routes.
     * start/stop routes api
     * choice (header) kafka, amq, randombeans, modelmapper
     * mimic irsingester logic
     * template.sendBody("controlbus:route?routeId=foo&action=start", null);
     * review log
     * commit from intellij - sonar
     * check actuator endpoints
     * different components
     * <p>
     * auto message converter using jackson - see spring messaging book. won't have to explicitly marshal unmarshal
     * delete xml files i don't use
     * configure jms connections, caching, templates, pooling so it is more production ready
     * keep doing notes and sample programs for camel book and learning about the technologies
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
                .marshal().json(JsonLibrary.Jackson)
                .to("direct:processData")
                // it isn't clear why '#' is needed betlow, but camel plugin doesn't like it when left off although
                // there is no error at runtime.
                .to("activemq:queue:randomdata_queue?connectionFactory=#jms");

        // note i use the activemq component.  it inherits from the jms component and uses the same properties
        // however it is more efficient than jms.
        from("activemq:queue:randomdata_queue?connectionFactory=#jms")
                .routeId("route.readFromJms")
                // note if i didn't unmarshal json text would print instead of the toString method
                // also i believe you can register a MessageConverter that puts the class name in a message
                // header and you don't need to specify the class explicitly. You wouldn't need to unmarshal
                // if the rest of the route was ok with dealing with json.
                .unmarshal().json(JsonLibrary.Jackson, Person.class)
                .log("Retrieved data from queue: ${body}");

        from("direct:generateRandomData.controlbus")
                .routeId("route.generateRandomData.controlbus")
                .log("action=${header.action}")
                // controlbus is an eip that allows you to start/stop/suspend/resume routes among other things.
                .toD("controlbus:route?routeId=route.generateRandomData&action=${header.action}")
                .transform(simple("Action taken on route: ${header.action}"));

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}