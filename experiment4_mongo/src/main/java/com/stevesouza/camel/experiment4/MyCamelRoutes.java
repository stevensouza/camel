package com.stevesouza.camel.experiment4;

import com.stevesouza.camel.experiment4.utils.ThrowException;
import org.apache.camel.Exchange;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MyCamelRoutes extends SpringRouteBuilder {

    /**
     * to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     * activemq - http://localhost:8161/
     */


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes

    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        from("timer:foo?period={{timer.publisher}}")
                .routeId("route.generateData")
                .setBody(() -> GenerateData.createPerson())
//                .marshal().json(JsonLibrary.Jackson)
                .to("direct:write_to_mongodb");

        // note mongoClientConnectionBean is a bean defined in CamelXmlApplication that has connection info for the mongodb server
        // note mongo converts the pojo directly to bson and saves fine. This is probably due to including one of the json libraries in the
        // pom.
        from("direct:write_to_mongodb")
                .routeId("route.toMongoDb")
                .log("writing to mongodb ${body}")
                .to("mongodb:mongoClientConnectionBean?database=testdb&collection=people&operation=insert");  // save=upsert, could also use insert

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}