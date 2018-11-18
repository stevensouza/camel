package com.stevesouza.xml;

import org.apache.camel.Body;
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

@Component("XSDValidatorBean")
public class XSDValidatorBean {

    private  final String NIEM_CORE_XSD = "https://release.niem.gov/niem/niem-core/4.0/niem-core.xsd";


    // Per the javadocs Schema is threadsafe and it is encouraged to be reused.  If you don't reuse then the url is downloaded each
    // time and in the case of niem-core.xsd takes about 5 seconds.
    private Schema niemSchema;

    public void validate(@Body File xmlFile) throws  Exception {
        try {
            URL schemaFile = new URL(NIEM_CORE_XSD);

            if (niemSchema==null) {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                niemSchema = factory.newSchema(schemaFile);
            }
            // Note I think calling factory.newSchema() allows you to validate with the schema as defined in the xml file.
            //      * <h2>W3C XML Schema 1.0</h2>
            //     * <p>
            //     * For XML Schema, this method creates a {@link Schema} object that
            //     * performs validation by using location hints specified in documents.
            // to validate against multiple xml schema I believe you would call:
            //       public abstract Schema newSchema(Source[] schemas) throws SAXException;
            // see here for further info: https://stackoverflow.com/questions/7009285/xml-validation-using-multiple-xsds/7009977#7009977
            // one api said this about newSchema() without args
            // newSchema
            //public Schema newSchema()
            //Returns the Schema object containing all the schema components that have been loaded using the newSchema(javax.xml.transform.Source[])
            // method. If no schema components have been loaded, the schema can still be used to validate source documents provided that they
            // specify the location of the required schema components using the xsi:schemaLocation attribute.

            Validator validator = niemSchema.newValidator();
            validator.validate(new StreamSource(xmlFile));
        } catch (IOException e){
            System.out.println("Exception: "+e.getMessage());
            throw e;
        } catch(SAXException e1){
            System.out.println("SAX Exception: "+e1.getMessage());
            throw e1;
        }

    }
}