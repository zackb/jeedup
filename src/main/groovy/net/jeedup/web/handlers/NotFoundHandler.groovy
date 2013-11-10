package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import io.undertow.util.StatusCodes
import net.jeedup.web.Endpoint
import net.jeedup.web.Response

import static net.jeedup.web.Response.TEXT

/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class NotFoundHandler {

    @Endpoint('404')
    Response notFound() {
        return TEXT('Not Found').withStatus(StatusCodes.NOT_FOUND)
    }
}
