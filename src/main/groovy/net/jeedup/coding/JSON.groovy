package net.jeedup.coding

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class JSON {

    private static final ObjectMapper mapper
    protected static final Class mapClass

    static {
        mapper = new ObjectMapper()
        configureMapper(mapper)

        mapClass = new HashMap<String, Object>().getClass()
    }

    static void configureMapper(ObjectMapper objectMapper) {

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
        //.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        //.enableDefaultTyping()
    }

    static String encode(Object obj) {
        return mapper.writeValueAsString(obj)
    }

    static <T> T decodeObject(String data, Class<T> clazz) {
        return (T)mapper.readValue(data.getBytes('UTF-8'), clazz)
    }

    static <T> List<T> decodeList(String data, Class<T> clazz) {
        List<T> result = (List<T>)mapper.readValue(data, mapper.getTypeFactory().constructCollectionType(List.class, clazz))
        return result
    }

    static Map decode(String data) {
        return (Map)decodeObject(data, mapClass)
    }
}
