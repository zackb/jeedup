package net.jeedup.web.auth

import groovy.transform.CompileStatic
import io.undertow.security.idm.Account

import java.security.Principal

/**
 * User: zack
 * Date: 11/22/13
 */
@CompileStatic
class JeedupAccount implements Account {
    private String id

    public JeedupAccount(String id) {
        this.id = id
    }

    @Override
    public Principal getPrincipal() {
        return new JeedupPrincipal()
    }

    @Override
    public Set<String> getRoles() {
        return Collections.emptySet()
    }
}
