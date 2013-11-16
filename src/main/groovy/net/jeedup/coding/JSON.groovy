package net.jeedup.coding

import com.fasterxml.jackson.core.JsonParser
import groovy.transform.CompileStatic
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class JSON {

    private static final ObjectMapper mapper
    private static final Class mapClass

    static {
        mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
            //.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            //.enableDefaultTyping()

        mapClass = new HashMap<String, Object>().getClass()
    }

    static String encode(Object obj) {
        return mapper.writeValueAsString(obj)
    }

    static <T> T decodeObject(String data, Class clazz) {
        return (T)mapper.readValue(data.getBytes('UTF-8'), clazz)
    }

    static Map decode(String data) {
        return (Map)decodeObject(data, mapClass)
    }
}
