package net.jeedup.web.render

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import groovy.transform.CompileStatic

import java.nio.charset.Charset

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
class HTMLRender extends Render {

    @Override
    void render(OutputStream out) {
        Object data = response.data
        if (data instanceof String) {
            out.write(data.getBytes(Charset.forName('UTF-8')))
        } else if (response.view) {
            StringWriter writer = new StringWriter()
            MustacheFactory mf = new DefaultMustacheFactory()
            Mustache mustache = mf.compile("html/${response.view}")
            mustache.execute(writer, data)
            writer.flush()
            out.write(writer.toString().getBytes('UTF-8'))
        }
    }
}
