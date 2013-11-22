package net.jeedup.web.auth

import groovy.transform.CompileStatic
import io.undertow.security.idm.Account
import io.undertow.security.idm.Credential
import io.undertow.security.idm.IdentityManager

/**
 * User: zack
 * Date: 11/22/13
 */
@CompileStatic
class JeedupIdentityManager implements IdentityManager {

    @Override
    public Account verify(Account account) {
        println 'VERIFY'
        return account
    }

    @Override
    public Account verify(String id, Credential credential) {
        println 'ID = ' + id
        return getAccount(id)
    }

    @Override
    public Account verify(Credential credential) {
        return null
    }

    private Account getAccount(final String id) {
        println 'GETACCOUNT'
        return new JeedupAccount(id)
    }
}
