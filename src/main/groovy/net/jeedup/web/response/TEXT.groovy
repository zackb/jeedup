package net.jeedup.web.response

import groovy.transform.CompileStatic
import net.jeedup.web.Response

import java.nio.charset.Charset

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class TEXT extends Response {
    public TEXT() {
        contentType('text/plain')
    }

    @Override
    void render(OutputStream out) {
        out.write(((String)data).getBytes(Charset.forName("UTF-8")))
    }
}
