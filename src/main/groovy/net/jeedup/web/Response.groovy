package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.util.StatusCodes
import net.jeedup.web.response.HTML
import net.jeedup.web.response.JSON
import net.jeedup.web.response.TEXT

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
abstract class Response {

    public String contentType
    public int status
    protected Object data

    //TODO cache headers

    public static JSON JSON() {
        return (JSON)new JSON()
                .withStatus(StatusCodes.OK)
    }

    public static HTML HTML() {
        return (HTML)new HTML()
                .withStatus(StatusCodes.OK)
    }

    public static TEXT TEXT() {
        return (TEXT)new TEXT()
                .withStatus(StatusCodes.OK)
    }

    public static JSON JSON(Object data) {
        return (JSON)JSON().withData(data)
    }

    public static HTML HTML(Object data) {
        return (HTML)HTML().withData(data)
    }

    public static TEXT TEXT(Object data) {
        return (TEXT)TEXT().withData(data)
    }

    public Response withContentType(String contentType) {
        this.contentType = contentType
        return this
    }

    public Response withStatus(int status) {
        this.status = status
        return this
    }

    public Response withData(Object data) {
        this.data = data
        return this
    }

    abstract void render(OutputStream out)
}
