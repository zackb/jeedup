package net.jeedup.web.auth

import groovy.transform.CompileStatic

import java.security.Principal

/**
 * User: zack
 * Date: 11/22/13
 */
@CompileStatic
class JeedupPrincipal implements Principal {
    @Override
    public String getName() {
        return 'ZAZAZA'
    }
}
