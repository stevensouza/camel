package com.stevesouza.camel.experiment3.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class MiscUtils {

    private MiscUtils() {
    }

    private static ModelMapper modelMapper = new ModelMapper();


    public static URL getResourceURL(String fileName) {
        URL url = MiscUtils.class.getClassLoader().getResource(fileName);
        if (null == url) {
            throw new MiscUtilsException(new FileNotFoundException(fileName));
        }
        return url;
    }

    public static String readFile(URL urlOfFile) throws IOException {
        return IOUtils.toString(urlOfFile, "UTF-8");
    }

    public static String readResourceFile(String fileName) throws IOException {
        return readFile(getResourceURL(fileName));
    }


    public static <T> T convert(Object convertFrom, Class<T> destinationType) {
        return modelMapper.map(convertFrom, destinationType);
    }

    public static <T> T convert(Object convertFrom, TypeToken<T> typeToken) {
        return modelMapper.map(convertFrom, typeToken.getType());
    }

    public static <T> T convert(Object convertFrom, T convertTo) {
        modelMapper.map(convertFrom, convertTo);
        return convertTo;
    }

    public static String toJsonString(Object pojo) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(pojo);
        } catch (IOException e) {
            throw new MiscUtilsException("Unable to convert the following object to json: " + pojo, e);
        }
    }

    public static String toJsonStringPrettyPrint(Object pojo) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pojo);
        } catch (IOException e) {
            throw new MiscUtilsException("Unable to convert the following object to json: " + pojo, e);
        }
    }

    public static <T> T toObjectFromJsonString(String json, Class<T> destinationType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, destinationType);
        } catch (IOException e) {
            throw new MiscUtilsException("Unable to convert the json string to the given object type: " + destinationType, e);
        }
    }


    /**
     * This is a deeper test of the full web of objects being equal.  They are being
     * tested as json as this test is possible in json format.  If they were pojos the
     * test for equality would fail as the vo has fewer fields than the entity. If the following json documents
     * were compared they would be considered equal.
     * actual: {"name":"steve", "salary":50}
     * expected: {"name":"steve", "salary":50, "favoriteNumbers":[1,2,3,4]}
     * <p>
     * if they are flipped then an exception would be thrown.
     *
     * @param actual   must have as many or fewer fields than the expected json: {"name":"steve", "salary":50}
     * @param expected {"name":"steve", "salary":50, "favoriteNumbers":[1,2,3,4]}
     */

    public static void assertLenientJsonEquality(Object actual, Object expected) {
        try {
            String actualJson = MiscUtils.toJsonString(actual);
            String expectedJson = MiscUtils.toJsonString(expected);
            JSONAssert.assertEquals("Values of fields that exist in both json documents do not match.", actualJson, expectedJson, JSONCompareMode.LENIENT);
        } catch (JSONException e) {
            throw new MiscUtilsException(e);
        }
    }

    /**
     * Use reflection to call collection.clear() on any instance variables that are collections
     * in the passed in Object. Note there needs to be a getCollectionName method name associated with
     * collectionName variable in Object.
     * <p>
     * This is used primarily to update entities.
     *
     * @param obj
     * @throws Exception
     */

    public static void clearCollections(Object obj) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(obj);

        try {
            for (PropertyDescriptor field : descriptors) {
                // if the field holds a collection then call its clear method.
                if (Collection.class.isAssignableFrom(field.getPropertyType())) {
                    Collection collection = null;

                    collection = (Collection) field.getReadMethod().invoke(obj);

                    collection.clear();
                }
            }
        } catch (Exception e) {
            throw new MiscUtilsException(e);
        }
    }


    public static class MiscUtilsException extends RuntimeException {
        public MiscUtilsException(Throwable cause) {
            super(cause);
        }

        public MiscUtilsException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}
