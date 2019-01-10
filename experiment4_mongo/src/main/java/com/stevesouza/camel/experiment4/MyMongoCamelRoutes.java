package com.stevesouza.camel.experiment4;

import com.mongodb.client.model.Filters;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.mongodb3.MongoDbConstants;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyMongoCamelRoutes extends SpringRouteBuilder {

    /**
     * to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     * activemq - http://localhost:8161/
     * <p>
     * mongodb3 component - https://github.com/apache/camel/blob/master/components/camel-mongodb3/src/main/docs/mongodb3-component.adoc
     * drill component - https://github.com/apache/camel/blob/master/components/camel-drill/src/main/docs/drill-component.adoc
     */


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes

    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        //
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        from("timer:foo?period={{timer.publisher}}")
                .routeId("route.generateData")
                .setBody(() -> GenerateData.createPerson())
        //        .marshal().json(JsonLibrary.Jackson)
                .to("direct:write_to_mongodb");

        // note mongoClientConnectionBean is a bean defined in CamelXmlApplication that has connection info for the mongodb server
        // note mongo converts the pojo directly to bson and saves fine. This is probably due to including one of the json libraries in the
        // pom.
        // https://github.com/apache/camel/blob/master/components/camel-mongodb3/src/main/docs/mongodb3-component.adoc
        from("direct:write_to_mongodb")
                .routeId("route.writeToMongoDb")
                .log("writing to mongodb ${body}")
                .to("mongodb3:mongoClientConnectionBean?database=testdb&collection=people&operation=save");  // save=upsert, could also use insert

        // count # of people in collection
        from("timer:foo?period={{timer.count}}")
                .routeId("route.count")
                .to("mongodb3:mongoClientConnectionBean?database=testdb&collection=people&operation=count")
                .log("Count of Person objects in mongodb=${body}");

        // count # of people with age<5
        from("timer:foo?period={{timer.countLt5}}")
                .routeId("route.countLt5")
                .setHeader(MongoDbConstants.CRITERIA, () -> Filters.lt("age", 5)) // provide a Supplier
                .to("mongodb3:mongoClientConnectionBean?database=testdb&collection=people&operation=count")
                .log("Count of Person objects with age<5 in mongodb=${body}");

        // findAll people with age<=2
        from("timer:foo?period={{timer.findAllLte2}}")
                .routeId("route.findAllLte2")
                .setHeader(MongoDbConstants.CRITERIA, () -> Filters.lte("age", 2))
                .to("mongodb3:mongoClientConnectionBean?database=testdb&collection=people&operation=findAll")
                .log("Find all Person objects with age<=2 in mongodb=${body}");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}