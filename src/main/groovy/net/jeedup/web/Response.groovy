package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.util.StatusCodes
import net.jeedup.web.response.HTML
import net.jeedup.web.response.JSON

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
abstract class Response {

    public String contentType
    public int status

    //TODO cache headers

    public static JSON JSON() {
        return (JSON)new JSON()
                .withStatus(StatusCodes.OK)

    }

    public static HTML HTML() {
        return (HTML)new HTML()
                .withStatus(StatusCodes.OK)
    }


    public Response withContentType(String contentType) {
        this.contentType = contentType
        return this
    }

    public Response withStatus(int status) {
        this.status = status
        return this
    }
}
