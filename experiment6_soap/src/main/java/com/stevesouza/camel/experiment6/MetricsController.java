package com.stevesouza.camel.experiment6;

import com.jamonapi.MonitorFactory;
import org.apache.camel.component.metrics.routepolicy.MetricsRegistryMBean;
import org.aspectj.lang.Aspects;
import org.automon.implementations.Jamon;
import org.automon.implementations.Metrics;
import org.automon.implementations.OpenMon;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;


@RestController
@RequestMapping
public class MetricsController {

    // use this when you are getting mbeans in the same jvm the server executes. Alternatively you can get a remote
    // connection.
    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    /**
     * if automon.properties is configured for jamon then this will have data in it.
     * examples
     * - default descending on hits column - http://localhost:8080/jamon
     * - descending on average - http://localhost:8080/jamon?colIndex=3&sortOrder=desc
     * - descending on average - http://localhost:8080/jamon?colIndex=3
     * - descending on total - http://localhost:8080/jamon?colIndex=4
     * - descending on max - http://localhost:8080/jamon?colIndex=8
     *
     * @param colIndex  starts at 0
     * @param sortOrder asc or desc
     * @return an html report of jamon stats
     */
    @GetMapping(value = "/jamon", produces = MediaType.TEXT_HTML_VALUE)
    public String jamon(
            @RequestParam(defaultValue = "2") Integer colIndex, // sort on hits column descending by default
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return MonitorFactory.getRootMonitor().getReport(colIndex, sortOrder);
    }

    /**
     * @return will return camel metrics, but also could return automon metrics if automon.properties is configured to use
     * metrics.
     * @throws MalformedObjectNameException
     */
    @GetMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public String metrics() throws MalformedObjectNameException {
        return getMetricsJmxService().dumpStatisticsAsJson();
    }

    /**
     * Used to test automon tracking an exception
     * @return
     */
    @GetMapping(value = "/exception", produces = MediaType.APPLICATION_JSON_VALUE)
    public String exception() {
        if (true) {
            throw new MyMetricsException();
        }
        return "should not get here";
    }

    /**
     * get camel metrics via jmx
     */
    public MetricsRegistryMBean getMetricsJmxService() throws MalformedObjectNameException {
        String METRICS = "org.apache.camel:context=camel.experiments6,type=services,name=MetricsRegistryService";
        MetricsRegistryMBean metricsRegistryService = JMX.newMXBeanProxy(mBeanServer, new ObjectName(METRICS), MetricsRegistryMBean.class);
        return metricsRegistryService;
    }


    public static OpenMon getOpenMon() {
        // get aspect singleton and ensure that camel and the aspect use the same metrics registry
        MySpringAspect mySpringAspect = Aspects.aspectOf(MySpringAspect.class);
        return mySpringAspect.getOpenMon();

    }

    public static Metrics getMetrics() {
        OpenMon openMon = getOpenMon();
        return (openMon instanceof Metrics) ? (Metrics) openMon : null;
    }

    public static Jamon getJamon() {
        OpenMon openMon = getOpenMon();
        return (openMon instanceof Metrics) ? (Jamon) openMon : null;
    }

    static class MyMetricsException extends RuntimeException {
        public MyMetricsException() {
            super("just testing to see if exceptions is tracked by automon");
        }
    }
}
