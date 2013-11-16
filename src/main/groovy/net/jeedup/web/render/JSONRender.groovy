package net.jeedup.web.render

import groovy.transform.CompileStatic
import net.jeedup.net.http.JSON

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
class JSONRender extends Render {

    @Override
    void render(OutputStream out) {
        if (!response.data) {
            return
        }
        out.write(JSON.encode(response.data).getBytes('UTF-8'))
    }

}
