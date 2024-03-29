package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import io.undertow.util.StatusCodes
import net.jeedup.net.http.Response
import net.jeedup.web.Endpoint

import static Response.HTML

/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class NotFoundHandler {

    @Endpoint('404')
    Response notFound() {
        return HTML('Not Found').status(StatusCodes.NOT_FOUND)
    }
}
