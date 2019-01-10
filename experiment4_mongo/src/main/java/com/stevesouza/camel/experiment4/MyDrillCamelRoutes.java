package com.stevesouza.camel.experiment4;

import org.apache.camel.component.drill.DrillConnectionMode;
import org.apache.camel.component.drill.DrillConstants;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyDrillCamelRoutes extends SpringRouteBuilder {

    /**
     * to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     * activemq - http://localhost:8161/
     * <p>
     * mongodb3 component - https://github.com/apache/camel/blob/master/components/camel-mongodb3/src/main/docs/mongodb3-component.adoc
     */


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes

    @Override
    public void configure() {

        // use apache drill to query mongodb

        // count # of people in collection
        String drillCountQuery = "select count(*) as numPeople from mongo.testdb.people";
        from("timer:foo?period={{timer.count}}")
                .routeId("route.drillCount")
                .setHeader(DrillConstants.DRILL_QUERY, constant(drillCountQuery))
                .to(String.format("drill://localhost?mode=%s&port=%s", DrillConnectionMode.DRILLBIT.name(),  "31010")) // default drill port
                .log(String.format("Apache drill count: %s = ${body}", drillCountQuery));

        // findAll people with age<=2
        String drillAgeLte2Query = "select * from mongo.testdb.people where age<=2";
        from("timer:foo?period={{timer.count}}")
                .routeId("route.drillFindAllLte2")
                .setHeader(DrillConstants.DRILL_QUERY, constant(drillAgeLte2Query))
                .to(String.format("drill://localhost?mode=%s&port=%s", DrillConnectionMode.DRILLBIT.name(),  "31010")) // default drill port
                .log(String.format("Apache drill find all Person objects with age<=2 in mongodb: %s = ${body}", drillAgeLte2Query));

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}