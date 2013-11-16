package net.jeedup.web.render

import groovy.transform.CompileStatic
import net.jeedup.net.http.HTML
import net.jeedup.net.http.JSON
import net.jeedup.net.http.Response

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
abstract class Render {

    protected Response response

    public Render() {
    }

    public static Render forResponse(Response response) {
        Render render = null
        if (response instanceof HTML) {
            render = new HTMLRender()
        } else if (response instanceof JSON) {
            render = new JSONRender()
        } else {
            render = new TEXTRender()
        }

        render.response = response
        return render
    }

    public abstract void render(OutputStream out)
}
