package net.jeedup.net.http

import groovy.transform.CompileStatic

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
}
