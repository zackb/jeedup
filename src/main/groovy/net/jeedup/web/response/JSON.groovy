package net.jeedup.web.response

import net.jeedup.web.Response

/**
 * User: zack
 * Date: 11/10/13
 */
class JSON extends Response {
    public JSON() {
        withContentType('application/json;charset=UTF-8')
    }
}
