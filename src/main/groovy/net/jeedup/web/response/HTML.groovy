package net.jeedup.web.response

import groovy.transform.CompileStatic
import net.jeedup.web.Response

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class HTML extends Response {

    public HTML() {
        withContentType('text/html')
    }

    @Override
    void render(OutputStream out) {
    }
}
