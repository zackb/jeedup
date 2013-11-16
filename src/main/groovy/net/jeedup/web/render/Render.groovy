package net.jeedup.web.render

import groovy.transform.CompileStatic
import net.jeedup.net.http.Response

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
abstract class Render {

    protected Response response

    public static Render forResponse(Response response) {
        Render render = null
        String contentType = response.contentType
        if (contentType) {
            contentType = contentType.toLowerCase()
            if (contentType.contains('json')) {
                render = new JSONRender()
            } else if (contentType.contains('html')) {
                render = new HTMLRender()
            } else {
                render = new TEXTRender()
            }
        }

        render.response = response

        return render

    }

    public abstract void render(OutputStream out)
}
