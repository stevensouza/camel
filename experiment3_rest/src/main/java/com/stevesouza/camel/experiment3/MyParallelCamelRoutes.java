package com.stevesouza.camel.experiment3;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MyParallelCamelRoutes extends SpringRouteBuilder {

    /**
     * <p>
     * to view hawt io (camel routes, jmx) - http://localhost:8080/actuator/hawtio/
     * activemq - http://localhost:8161/
     */


    // @formatter:off - disable intellij's reformat command from messing up indentation in camel routes
    /** shows rest, transform, seda, multicast, parallelProcessing, multiple destinations

     */
    @Override
    public void configure() {

         // note you need to call 'route()' to access the underlying route to call transform and many of the other eip's.
         // localhost:8080/rest/parallel/steve
         // data is sent to 2 seda queues (1 parallel, 1 single threaded) and these queues submit to a 3rd seda queue.  really
         // no point of this except to play with the api.
         rest()
                .get("/parallel/{myname}")
                .produces(MediaType.TEXT_PLAIN_VALUE)
                .route().routeId("route.servlet.parallel")
                 .transform(simple("${header.myname}")) // transform sets the body of the message
                 .log("myname = ${header.myname}, body = ${body}")
                 .multicast().parallelProcessing()// submit 'to' in parallel
                 .to("seda:single_queue", "seda:parallel_queue")
                .transform(simple("hi world, I am parallel and my name is '${header.myname}'"));

         from("seda:single_queue")
                 .routeId("route.seda.single_queue")
                 .log("1) body=${body}")
                 .to("seda:merge_queue");


         from("seda:parallel_queue?concurrentConsumers=20")
                 .routeId("route.seda.parallel_queue")
                 .log("2) body=${body}")
                 .to("seda:merge_queue");

         // note you can also grab from multiple origins i.e. from("seda:queue1","seda:queue2")...
         from("seda:merge_queue")
                 .routeId("route.seda.merge_queue")
                 .transform(simple("twice(${body},  ${body})")) //doing this so i can look at changed body in hawtio trace
                 .log("3) body=${body}");

    }
    // @formatter:on - enable intellij's reformat command after having disabled it for the above camel routes


}