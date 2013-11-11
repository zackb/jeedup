package net.jeedup.web.response

import groovy.transform.CompileStatic
import net.jeedup.web.Response
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
class JSON extends Response {

    private static final ObjectMapper mapper

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
            .enableDefaultTyping()

    }

    public JSON() {
        withContentType('application/json;charset=UTF-8')
    }

    @Override
    void render(OutputStream out) {
        if (!data) {
            return
        }
        String str = mapper.writeValueAsString(data)
        out.write(str.getBytes('UTF-8'))
    }
}
