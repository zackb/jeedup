package net.jeedup.web.response

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import groovy.transform.CompileStatic
import net.jeedup.web.Response

import java.nio.charset.Charset

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class HTML extends Response {

    public String view

    public HTML() {
        contentType('text/html')
    }

    public HTML view(String view) {
        this.view = view
        if (this.view && !this.view.endsWith('.html')) {
            this.view = this.view + '.html'
        }

        return this
    }

    @Override
    void render(OutputStream out) {
        if (data instanceof String) {
            out.write(data.getBytes(Charset.forName("UTF-8")))
        } else if (view) {
            StringWriter writer = new StringWriter()
            MustacheFactory mf = new DefaultMustacheFactory()
            Mustache mustache = mf.compile("html/${view}")
            mustache.execute(writer, data)
            writer.flush()
            out.write(writer.toString().getBytes('UTF-8'))
        }
    }
}
