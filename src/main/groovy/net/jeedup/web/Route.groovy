package net.jeedup.web

import groovy.transform.CompileStatic
import net.jeedup.net.http.Request
import net.jeedup.net.http.Response

import java.lang.reflect.Method

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class Route {
    String path
    Object handler
    Method action

    Response invoke(Request request) {
        Response response = null
        Class<?>[] types = action.getParameterTypes()
        if (types.size() == 1) {
            if (Request.class.isAssignableFrom(types[0])) {
                response = (Response)action.invoke(handler, request)
            } else {
                response = (Response)action.invoke(handler, request.data)
            }
        } else {
            response = (Response)action.invoke(handler)
        }

        return response
    }
}
