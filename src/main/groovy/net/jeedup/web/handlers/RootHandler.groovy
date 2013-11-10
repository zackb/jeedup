package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.web.Endpoint
import net.jeedup.web.Response

import static net.jeedup.web.Response.JSON

/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class RootHandler {

    @Endpoint('test')
    Response test(Map data) {
        return JSON()
    }
}
