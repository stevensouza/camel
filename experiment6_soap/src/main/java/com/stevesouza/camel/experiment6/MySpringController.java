package com.stevesouza.camel.experiment6;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Note the following controller accepts 2 integer arguments and uses a camel
 * web services route to call an external api to add, subtract, multiply, or
 * divide the 2 numbers.  Most of the endpoints take a json array which is automatically
 * converted to a List.  The cxf web service takes a List if the web service
 * endpoint takes mutliple endpoints.  Note if the endpoint can take a specific
 * object I suspect that could be passed directly (i.e. if the Add object could be submitted
 * directly.
 *
 * The endpoints that have postmapping take a json payload of an array (unnamed) with 2 values.  Exactly
 * like this: [5,6]. In the case of add being called the answer would be 11, and in the case of multiply
 * the answer would be 30 etc.
 *
 * Here are the endpoints that would receive the above mentioned array json
 *  http://localhost:8080/math/add
 *  http://localhost:8080/math/subtract
 *  http://localhost:8080/math/multiply
 *  http://localhost:8080/math/divide
 *
 * The following endpoint is the only one defined for a GET request:
 *   http://localhost:8080/math/addwithparams?a=4&b=5
 *
 *   See https://github.com/apache/camel/blob/master/components/camel-cxf/src/main/docs/cxf-component.adoc
 *   for more info on the camel cxf endpoint.  Note alternatively the spring webservices component
 *   could be used https://github.com/apache/camel/blob/master/components/camel-spring-ws/src/main/docs/spring-ws-component.adoc
 */

@RestController
@RequestMapping(value = "/math")
@Slf4j
public class MySpringController {

    // note you could reuse the same FluentProducerTemplate and set the endpoint (probably template.to("direct:start")
    @EndpointInject(uri = "direct:math")
    private FluentProducerTemplate math;

    @PostMapping("/add")
    public String add(@RequestBody List<Integer> list) {
        return callSoapMathService(list, "Add");
    }

    @PostMapping("/subtract")
    public String subtract(@RequestBody List<Integer> list) {
        return callSoapMathService(list, "Subtract");
    }

    @PostMapping("/multiply")
    public String multiply(@RequestBody List<Integer> list) {
        return callSoapMathService(list, "Multiply");
    }

    @PostMapping("/divide")
    public String divide(@RequestBody List<Integer> list) {
        return callSoapMathService(list, "Divide");
    }

    /**
     * Adds integers a+b and returns the reponse
     *
     * example: http://localhost:8080/experiment6/addwithlist?a=13&b=17
     *
     * would return: response is 30
     *
     * @param a
     * @param b
     * @return
     */
    @GetMapping("/addwithparams")
    public String addWithList(@RequestParam("a") int a, @RequestParam("b") int b) {
        return callSoapMathService(build(a, b), "Add");
    }

    @PostMapping("/add2")
    public String add2(@RequestBody List<Integer> list) {
        return math
                .withHeader("operationName", "Add")
                .withBody(list)
                .to("direct:math2")
                .request(String.class);
    }

    private String callSoapMathService(@RequestBody List<Integer> list, String operationName) {
        return math
                .withHeader("operationName", operationName)
                .withBody(list)
                .request(String.class);
    }


    private List<Integer> build(int a, int b) {
        List<Integer> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        return list;
    }


}
