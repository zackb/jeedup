package net.jeedup.coding

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
class BSON {

    private static final ObjectMapper mapper
    protected static final Class mapClass

    static {
        mapper = new ObjectMapper(new SmileFactory())
        JSON.configureMapper(mapper)

        mapClass = new HashMap<String, Object>().getClass()
    }

    static byte[] encode(Object obj, boolean compress = false) {
        byte[] bytes = mapper.writeValueAsBytes(obj)
        if (compress)
            bytes = GZIP.compress(bytes)
        return bytes
    }

    static <T> T decodeObject(byte[] bytes, Class<T> clazz, boolean compressed = false) {
        if (compressed) {
            bytes = GZIP.decompress(bytes)
        }
        return (T)mapper.readValue(bytes, clazz)
    }

    static Map decode(byte[] bytes, boolean compressed = false) {
        return (Map)decodeObject(bytes, mapClass, compressed)
    }
}
