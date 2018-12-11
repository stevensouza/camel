package com.stevesouza.camel.experiment2;

import com.stevesouza.camel.experiment2.data.Person;
import com.stevesouza.camel.experiment2.utils.MiscUtils;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Configuration of test classes was taken primarily from here.  Documentation for spring boot testing using adviceWith is
 * weak.
 * <p>
 * https://tech.willhaben.at/testing-apache-camel-applications-with-spring-boot-da536568d9f7
 * <p>
 * Another source https://tech.willhaben.at/testing-apache-camel-applications-with-spring-boot-da536568d9f7
 */
@RunWith(CamelSpringBootRunner.class)
@UseAdviceWith
@SpringBootApplication
@SpringBootTest(classes = MyCamelRouteTest_WriteToAmq.class)
public class MyCamelRouteTest_WriteToAmq {

    @Autowired
    private FluentProducerTemplate fluentTemplate;

    @Autowired
    private ProducerTemplate template;

    @Autowired
    private ModelCamelContext context;


    @Test
    public void testWriteToAmq() throws Exception {
        RouteDefinition route = context.getRouteDefinition("route.generateAndWriteRandomDataToAmq");
        route.adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // disable startup of all routes as we only want to run the one under test which we will later reenable.
                context.getRouteDefinitions().forEach(RouteDefinition::noAutoStartup);

                // change the consumer to take 'direct' input so we don't have any external dependencies for the test.
                replaceFromWith("direct:data");
                // replace the 'to' output with a mock so we can test that it is receiving the expected output
                interceptSendToEndpoint("activemq:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:result");

                // restart the route under test.
                getOriginalRoute().autoStartup(true);

            }
        });
        // @UseAdviceWith disables the camelContext startup, so you can manipulate the routes, so we will now start it for test
        context.start();

        // get the mockendpoint so we can set expectations for the asserts
        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(2);
        mock.expectsNoDuplicates().body();
        mock.allMessages().body().isNotNull();
        // nonsensical test just to give an idea how it would work
        mock.allMessages().header("delme").isNull();
        mock.allMessages().header("hello").isEqualTo("world");

        // note it doesn't matter what is in body as the body/Person object is generated in the route under test.
        String body = "anything";

        template.sendBodyAndHeader("direct:data", body, "hello", "world");
        fluentTemplate
                .withHeader("hello", "world")
                .to("direct:data")
                .withBody(body)
                .send();


        // asserts all mocks defined
        MockEndpoint.assertIsSatisfied(context);

        String json1 = mock.getExchanges().get(0).getMessage().getBody(String.class);
        assertThat(json1).contains("personDescriptionText");
        // no assertion set but an exception will be thrown if it can't convert the json into Person
        MiscUtils.convert(json1, Person.class);

    }


}
