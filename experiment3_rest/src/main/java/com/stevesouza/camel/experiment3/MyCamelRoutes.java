package com.stevesouza.camel.experiment3;

import com.stevesouza.camel.experiment3.utils.ThrowException;
import org.apache.camel.Exchange;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.json.simple.JsonObject;
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

        // handled(true) means that the exception won’t be propagated up the route to the caller.
        // it is like handling the exception in the catch block.  This also means that the original route
        // is broken and flow continues here to the end.
        // Sample output:
        //      HTTP status code: 500
        //      Message: Simulating a customer validation exception to show how camel maps exceptions to http status codes
        //      Stacktrace: com.stevesouza.camel.experiment3.utils.ThrowException$CustomerValidation: Simulating a customer validation exception to show how camel maps exceptions to http status codes
	    //      ...rest of stacktrace...
        //  probaby better to set headers and return a JsonObject with error info in body
        onException(Exception.class)
//            .handled(constant("${header.handled} == 'handled'").isEqualTo(true))
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(405))
            .setHeader("hello", constant("world"))
            .setHeader("myhttpheader", constant("myvalue"))
            .setHeader("error_message", simple("${exception.message}"))
            .setBody(
                    simpleF("HTTP status code: %s\nMessage: %s\nStacktrace: %s\nHandled: %s",
                    "${header.CamelHttpResponseCode}",
                    "${exception.message}",
                    "${exception.stacktrace}",
                    "${header.handled}")
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
        // available at endpoint: http://localhost:8080/rest/swagger
        restConfiguration()
                .apiContextPath("swagger")
                .apiContextRouteId("route.swagger");

        // using servlet component and not rest dsl. note could also use jetty as above
        // will use localhost:8080/rest/hello - using default tomcat server
        // default would be localhost:8080/rest/hello
         from("servlet://hello")
                .routeId("route.servlet.helloWorld")
                // will be started and stopped first. developers have from #'s 1 to 999.  with later versions camel is smarter and so you
                // need to do this less often
                .startupOrder(1)
                .transform().simple("hello world to {{MY_FIRST_NAME}} {{MY_LAST_NAME}}");

         // note you need to call 'route()' to access the underlying route to call transform and many of the other eip's.
         // localhost:8080/rest/hi
         rest()
                .get("/hi")
                 // swagger documentation - note you can also document parameters and more though it is a bit wordy
                    .description("endpoint that says hi")
                    .responseMessage().code(200).message("call to hi was successful").endResponseMessage()
                 // end swagger
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .route().routeId("route.servlet.hi")
                .transform(constant("hi world"));

        // returns random person object as either json or xml, but defaults to json
        // localhost:8080/rest/random
        rest()
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/random")
                .route().routeId("route.servlet.randomPerson")
                .tracing() // logs message as it is in each node
                // .bean(GenerateData.class) // This should work too.  Looks like you can act on it like a regular route too.
                .to("bean:generateData?method=getRandomPerson")
                // using log component to see how it prints. prints MEP, bodytype and body by default at info level
                .to("log:generated.person")
                .to("log:generated.person.showAll?showAll=true") // show full exchange info
                .log("log method: ${body}")
                // note the property must exist or an exception is thrown.
                .log("Showing how properties can be referenced (either OS, or application.properties. HOME=${properties:HOME}");


        // this will trigger onException mapping to status codes above i.e. http status 500 is mapped to the exception
        // localhost:8080/rest/exception/handled
        // localhost:8080/rest/exception/nothandled

        rest("/exception/{handled}")
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .get()
                .route().routeId("route.servlet.exception")
                .bean(ThrowException.class, "throwException");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}