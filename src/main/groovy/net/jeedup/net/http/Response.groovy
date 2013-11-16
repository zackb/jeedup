package net.jeedup.net.http

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class Response {

    public String contentType
    public int status
    public Object data
    public InputStream inputStream
    public Map<String, String> headers

    // hack just for html?
    public String view

    //TODO cache headers

    public static Response OK(Object data = null) {
        return new Response()
                .data(data)
                .status(200)
    }

    public static Response HTML(Object data = null, String view = null) {
        return OK(data)
                .contentType('text/html')
                .view(view)
    }


    public static Response TEXT(Object data = null) {
        return OK(data).contentType('text/plain')
    }

    public static Response JSON(Object data = null) {
        return OK(data).contentType('application/json;charset=UTF-8')
    }

    public Response contentType(String contentType) {
        this.contentType = contentType
        return this
    }

    public Response status(int status) {
        this.status = status
        return this
    }

    public Response data(Object data) {
        this.data = data
        return this
    }

    public Response view(String view) {
        this.view = view
        if (this.view && !this.view.endsWith('.html')) {
            this.view = this.view + '.html'
        }

        return this
    }
}
