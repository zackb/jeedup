package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.web.Endpoint

import static net.jeedup.net.http.Response.HTML

/**
 * User: zack
 * Date: 11/15/13
 */
@CompileStatic
class ZackHandler {

    @Endpoint('zack')
    def index(Map data) {
        HTML(null, 'zack/index')
    }

    @Endpoint('zack/about')
    def about(Map data) {
        HTML()
    }

    @Endpoint('zack/projects')
    def projects(Map data) {
        HTML()
    }
}
