package com.stevesouza.xml;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component("xmlxsd")
public class XSDValidator  {
//    public static void main(String[] args) {
//        XSDValidator validator = new XSDValidator();
//        if(args.length !=2){
//            System.out.println("Usage : XSDValidator <file-name.xsd> <file-name.xml>" );
//        } else {
//            boolean isValid = validator.validateXMLSchema(args[0],args[1]);
//
//            if(isValid){
//                System.out.println(args[1] + " is valid against " + args[0]);
//            } else {
//                System.out.println(args[1] + " is not valid against " + args[0]);
//            }
//
//        }
//    }

    public boolean validateXMLSchema(String xsdPath, String xmlPath) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new ClassPathResource(xsdPath).getFile());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ClassPathResource(xmlPath).getFile()));
        } catch (IOException e){
            System.out.println("Exception: "+e.getMessage());
            return false;
        } catch(SAXException e1){
            System.out.println("SAX Exception: "+e1.getMessage());
            return false;
        }

        return true;
    }

    // Per the javadocs Schema is threadsafe and it is encouraged to be reused.  If you don't reuse then the url is downloaded each
    // time and in the case of niem-core.xsd takes about 5 seconds.
    private Schema niemSchema;
    public boolean validateOnlineXMLSchema(String xsdUrl, String xmlPath) {
        try {
            URL schemaFile = new URL(xsdUrl);
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            if (niemSchema==null) {
                niemSchema = factory.newSchema(schemaFile);
            }
            //Schema schema = factory.newSchema(schemaFile);
            Validator validator = niemSchema.newValidator();
            validator.validate(new StreamSource(new ClassPathResource(xmlPath).getFile()));
        } catch (IOException e){
            System.out.println("Exception: "+e.getMessage());
            return false;
        } catch(SAXException e1){
            System.out.println("SAX Exception: "+e1.getMessage());
            return false;
        }

        return true;
    }
}