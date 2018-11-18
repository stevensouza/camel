package com.stevesouza.xml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Calendar;
import java.util.Date;

//@SpringBootApplication
public class NiemXmlxsdApplication {


	public static void main(String[] args) {
	    final String NIEM_CORE_XSD = "https://release.niem.gov/niem/niem-core/4.0/niem-core.xsd";
		ApplicationContext context = SpringApplication.run(NiemXmlxsdApplication.class, args);
        XSDValidator validator = context.getBean("xmlxsd", XSDValidator.class);
        for (int i=0;i<5;i++) {
            System.out.println();
            System.out.println("Start time=" + new Date());
            printStatus("person.xml",   validator.validateOnlineXMLSchema(NIEM_CORE_XSD, "person.xml"));
            printStatus("employee.xml", validator.validateOnlineXMLSchema(NIEM_CORE_XSD, "employee.xml"));
            printStatus("students.xml", validator.validateOnlineXMLSchema(NIEM_CORE_XSD, "students.xml"));
            System.out.println("End time=" + new Date());
        }

    }

    private static void printStatus(String xmlFile, boolean isValid) {
        if(isValid){
            System.out.println("  VALID - "+xmlFile + " is valid against the xsd ");
        } else {
            System.out.println("  NOT VALID - "+xmlFile + " is not valid against the xsd" );
        }
    }
}
