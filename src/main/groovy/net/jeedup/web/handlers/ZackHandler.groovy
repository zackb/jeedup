package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.web.Endpoint

import static net.jeedup.web.Response.HTML

/**
 * User: zack
 * Date: 11/15/13
 */
@CompileStatic
class ZackHandler {

    @Endpoint('zack/index')
    def index(Map data) {
        HTML()
    }
}
