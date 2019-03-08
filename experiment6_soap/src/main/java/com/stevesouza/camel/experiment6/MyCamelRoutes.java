package com.stevesouza.camel.experiment6;

//import com.stevesouza.camel.experiment6.data.Person;
//import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
//import org.apache.camel.model.dataformat.JsonLibrary;
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

    @Value("${camel.route.readFromKafka.from}")
    private String readFromKafkaFrom;

    @Value("${camel.route.processData.kafka.to}")
    private String processDataKafkaTo;

    @Value("${camel.route.processData.activemq.to}")
    private String processDataActiveMqTo;


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** Defined routes:
     *      - route.generateRandomData.controlbus - Called from rest controller to start/stop the main route
     *          (route.generateRandomData)
     *      - route.generateRandomData - generate person data and write to amq/kafka
     *
     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        from("direct:delme")
                .routeId("route.delme")
    .setBody(constant("12345"))
    .bean(AddBuilder.class)

    .setHeader("operationName", constant("Add"))

    .to("cxf://http://www.dneonline.com/calculator.asmx"
        + "?serviceClass=org.tempuri.CalculatorSoap"
        + "&wsdlURL=service.wsdl")
    .log("The response was ${body[0]}")
        .transform(simple("The response was ${body[0]}"));

        // Control main route
        from("direct:delmex")
                .routeId("route.delmex")
                .log("header.action=${header.action}")
                .transform(simple("Action taken on route: ${header.action}"));
//        from("direct:generateRandomData.controlbus")
//               .routeId("route.generateRandomData.controlbus")
//               .log("action=${header.action}")
//               // controlbus is an eip that allows you to start/stop/suspend/resume routes among other things.
//               .toD("controlbus:route?routeId=route.generateRandomData&action=${header.action}")
//               .transform(simple("Action taken on route: ${header.action}"));

        // Generate random Person data
        // could use timer or quartz instead of schedule component
//        from(generateRandomDataFrom)
//                .routeId("route.generateRandomData")
//                .noAutoStartup() // don't run route on app startup. The control bus below will start/stop it on demand
//                // The methods return value will be used to setBody(..)
//                .bean(GenerateData.class)
//                .log("Generated random data=${body}")
//                .marshal().json(JsonLibrary.Jackson)
//                .to("direct:processData");

        // Send data on to either amq or kafka depending on configuration
        // always assign a routeId for easy route identification
        //.noAutoStartup()
        // You can set a node description with: .description("my description")
//        from("direct:processData")
//              .routeId("route.processData")// set the route name and description which will display in hawtio
//              .routeDescription("Route that either sends message to kafka, or amq based on config property")
//              .log("Processing data=${body}")
//                .choice()
//                    .id("Choice: kafka or amq?")// the name for this 'choice' node which will show up in hawtio route diagram
//                    // getting property for spring application.properties file
//                    .when(simple("${properties:experiment6.broker_type} == 'kafka'"))
//                        .log("broker_type is kafka")
//                        .setHeader(KafkaConstants.KEY, constant("person json")) // Key of the message
//                        .toD(processDataKafkaTo)
//                    .otherwise()
//                        .log("broker type is activemq")
//                        //  '#jms' is used to look up the spring bean of that name to get the connectionFactory from
//                        .toD(processDataActiveMqTo)
//                .end();



        // note i use the activemq component.  it inherits from the jms component and uses the same properties
        // however it is more efficient than jms.
        // read from activemq
        // showing alternative way of defining from clause in a property
//         from(simple("{{camel.route.readFromActiveMq.from}}").getText())
//                .routeId("route.readFromActiveMq")
//                // note if i didn't unmarshal json text would print instead of the toString method
//                // also i believe you can register a MessageConverter that puts the class name in a message
//                // header and you don't need to specify the class explicitly. You wouldn't need to unmarshal
//                // if the rest of the route was ok with dealing with json.
//                .unmarshal().json(JsonLibrary.Jackson, Person.class)
//                .log("Retrieved data from amq queue: ${body}");

        // read data that was written to kafka
        // note apache website is outdated.  This seems to be more accurate:
        //   https://github.com/apache/camel/blob/master/components/camel-kafka/src/main/docs/kafka-component.adoc
//        from(readFromKafkaFrom)
//                .routeId("route.readFromKafka")
//                .log("Retrieved data from kafka topic: ${body}");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}