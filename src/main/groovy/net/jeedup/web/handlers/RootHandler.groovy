package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.web.Endpoint
import net.jeedup.web.Response

import static net.jeedup.web.Response.JSON
import static net.jeedup.web.Response.TEXT

/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class RootHandler {

    @Endpoint('json')
    Response test(Map data) {
        JSON(data)
    }

    @Endpoint('text')
    Response echo(Map data) {
        TEXT(data)
    }
}
