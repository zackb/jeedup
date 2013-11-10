package net.jeedup.web.response

import groovy.transform.CompileStatic
import net.jeedup.web.Response

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class JSON extends Response {
    public JSON() {
        withContentType('application/json;charset=UTF-8')
    }

    @Override
    void render(OutputStream out) {
    }
}
