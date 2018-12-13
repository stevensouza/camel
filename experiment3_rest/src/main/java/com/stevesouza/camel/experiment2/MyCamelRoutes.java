package com.stevesouza.camel.experiment2;

import com.stevesouza.camel.experiment2.utils.ThrowException;
import org.apache.camel.Exchange;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.http.MediaType;
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


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** Defined routes:

     */
    @Override
    public void configure() {
        // enable dropwizard metrics - not required
        // see hawt io 'Route Metrics' for jamon like data it collects.
        getContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        onException(Exception.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .setBody(
                    simpleF("HTTP status code: %s\nMessage: %s\nStacktrace: %s",
                    "${header.CamelHttpResponseCode}",
                    "${exception.message}",
                    "${exception.stacktrace}")
            );


        /*  If you use camel-jetty-starter can do this however then it loads both tomcat and jetty, so using 'servlet'
            component just reuses tomcat:

        from("jetty://http://0.0.0.0:{{camel.route.http.port}}/hello")
                .routeId("route.helloWorld")
                .transform().simple("hello world");
        */

        /* Could configure like the below, but instead I did it in application.properties:

       restConfiguration()
                .contextPath("rest"). // uses '/camel' by default the following are set in application properties or default is taken.
                .port(port)
                .bindingMode(RestBindingMode.json_xml)
                .dataFormatProperty("prettyPrint", "true");
                */

        // using servlet component and not rest dsl. note could also use jetty as above
        // will use localhost:8080/rest/hello - using default tomcat server
        // default would be localhost:8080/camel/hello
         from("servlet://hello")
                .routeId("route.servlet.helloWorld")
                .transform().simple("hello world");

         // note you need to call 'route()' to access the underlying route to call transform and many of the other eip's.
         rest()
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .get("/hi")
                .route().routeId("route.servlet.hi")
                .transform(constant("hi world"));

        // returns random person object as either json or xml, but defaults to json
        rest()
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/random")
                .route().routeId("route.servlet.randomPerson")
                // .bean(GenerateData.class) // This should work too.  Looks like you can act on it like a regular route too.
                .to("bean:generateData?method=getRandomPerson");

        // this will trigger onException mapping to status codes above i.e. http status 500 is mapped to the exception
        rest("/exception")
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .get()
                .route().routeId("route.servlet.exception")
                .bean(ThrowException.class, "throwException");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}