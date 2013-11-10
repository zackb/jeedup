package net.jeedup.web.response

import net.jeedup.web.Response

/**
 * User: zack
 * Date: 11/10/13
 */
class HTML extends Response {

    public HTML() {
        withContentType('text/html')
    }
}
