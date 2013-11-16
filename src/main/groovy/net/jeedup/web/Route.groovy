package net.jeedup.web

import groovy.transform.CompileStatic
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

    Response invoke(Map data) {
        Response response = null
        if (action.getParameterTypes().size() == 1)
            response = (Response)action.invoke(handler, data)
        else
            response = (Response)action.invoke(handler)

        return response
    }
}
