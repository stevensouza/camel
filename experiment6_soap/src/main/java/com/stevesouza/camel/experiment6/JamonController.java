package com.stevesouza.camel.experiment6;

import com.jamonapi.MonitorFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/jamon")
public class JamonController {
    /**
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
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String jamon(
            @RequestParam(defaultValue = "2") Integer colIndex, // sort on hits column descending by default
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return MonitorFactory.getRootMonitor().getReport(colIndex, sortOrder);
    }
}
