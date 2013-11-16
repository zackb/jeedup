package net.jeedup.web.render

import groovy.transform.CompileStatic

import java.nio.charset.Charset

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
class TEXTRender extends Render {
    @Override
    void render(OutputStream out) {
        out.write(((String)response.data).getBytes(Charset.forName('UTF-8')))
    }
}
