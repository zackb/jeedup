package net.jeedup.web.auth

import groovy.transform.CompileStatic
import io.undertow.server.HttpServerExchange
import io.undertow.server.session.SessionConfig

/**
 * User: zack
 * Date: 11/22/13
 */
@CompileStatic
class JeedupSessionConfig implements SessionConfig {
    @Override
    public void setSessionId(HttpServerExchange exchange, String sessionId) {
    }

    @Override
    public void clearSession(HttpServerExchange exchange, String sessionId) {
    }

    @Override
    public String findSessionId(HttpServerExchange exchange) {
        return null
    }

    @Override
    public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
        return null
    }

    @Override
    public String rewriteUrl(String originalUrl, String sessionId) {
        return null
    }
}
