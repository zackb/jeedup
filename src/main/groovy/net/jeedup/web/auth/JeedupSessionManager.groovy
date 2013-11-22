package net.jeedup.web.auth

import groovy.transform.CompileStatic
import io.undertow.security.api.AuthenticatedSessionManager
import io.undertow.server.HttpServerExchange
import io.undertow.server.session.Session
import io.undertow.server.session.SessionConfig
import io.undertow.server.session.SessionListener
import io.undertow.server.session.SessionManager

/**
 * User: zack
 * Date: 11/22/13
 */
@CompileStatic
class JeedupSessionManager implements SessionManager, AuthenticatedSessionManager {

    @Override
    AuthenticatedSessionManager.AuthenticatedSession lookupSession(HttpServerExchange exchange) {
        return null
    }

    @Override
    void start() {
    }

    @Override
    void stop() {
    }

    @Override
    Session createSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return null
    }

    @Override
    Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return null
    }

    @Override
    Session getSession(String sessionId) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void registerSessionListener(SessionListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void removeSessionListener(SessionListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void setDefaultSessionTimeout(int timeout) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Set<String> getTransientSessions() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Set<String> getActiveSessions() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Set<String> getAllSessions() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
