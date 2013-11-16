package net.jeedup.net.http

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class TEXT extends Response {
    public TEXT() {
        contentType('text/plain')
    }
}
