package com.stevesouza.xml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//@SpringBootApplication
public class XmlxsdApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(XmlxsdApplication.class, args);
        XSDValidator validator = context.getBean("xmlxsd", XSDValidator.class);
        String xsdFile;
        String xmlFile;
        boolean isValid;
        if(args.length !=2){
            System.out.println("Usage : XSDValidator <file-name.xsd> <file-name.xml>" );
            System.out.println();
            System.out.println();

            xsdFile="students.xsd";
            xmlFile="students.xml";
            String invalidXmlFile="students_invalid.xml";
            printStatus(xmlFile, validator.validateXMLSchema(xsdFile, xmlFile));
            printStatus(invalidXmlFile, validator.validateXMLSchema(xsdFile, invalidXmlFile));

        } else {
            printStatus(args[1], validator.validateXMLSchema(args[0],args[1]));

        }

    }

    private static void printStatus(String xmlFile, boolean isValid) {
        if(isValid){
            System.out.println("1) "+xmlFile + " is valid against the xsd ");
            System.out.println();
        } else {
            System.out.println("2) "+xmlFile + " is not valid against the xsd" );
            System.out.println();
        }
    }
}
