package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.Undertow
import io.undertow.predicate.Predicates
import io.undertow.security.api.AuthenticationMechanism
import io.undertow.security.api.AuthenticationMode
import io.undertow.security.handlers.AuthenticationCallHandler
import io.undertow.security.handlers.AuthenticationConstraintHandler
import io.undertow.security.handlers.AuthenticationMechanismsHandler
import io.undertow.security.handlers.SecurityInitialHandler
import io.undertow.security.idm.IdentityManager
import io.undertow.security.impl.BasicAuthenticationMechanism
import io.undertow.security.impl.CachedAuthenticatedSessionMechanism
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.encoding.ContentEncodingRepository
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.server.handlers.encoding.GzipEncodingProvider
import io.undertow.server.session.SessionAttachmentHandler
import net.jeedup.persistence.sql.DataSources
import net.jeedup.web.auth.JeedupIdentityManager
import net.jeedup.web.auth.JeedupSessionConfig
import net.jeedup.web.auth.JeedupSessionManager

@CompileStatic
class Jeedup {
    private Undertow server

    public Jeedup() {
    }

    public void start() {

        // configure datasources and data models
        DataSources.getInstance()

        server = Undertow.builder()
                .addListener(Config.port(), Config.host())
                .setHandler(createHttpHandler())
                .setWorkerThreads(400)
                .build()
        server.start()
    }

    private HttpHandler createHttpHandler() {
        final jeedupHandler = new JeedupHandler()

        final EncodingHandler handler = new EncodingHandler(new ContentEncodingRepository()
                .addEncodingHandler('gzip', new GzipEncodingProvider(), 50, Predicates.maxContentSize(5)))
                .setNext(jeedupHandler)

        return handler //addSecurity(handler, new JeedupIdentityManager())
    }

    private static HttpHandler addSecurity(final HttpHandler toWrap, final IdentityManager identityManager) {
        HttpHandler handler = toWrap;
        handler = new AuthenticationCallHandler(handler)
        handler = new AuthenticationConstraintHandler(handler)
        final List<AuthenticationMechanism> mechanisms = [new CachedAuthenticatedSessionMechanism(), new BasicAuthenticationMechanism("My Realm")] as List<AuthenticationMechanism>
        handler = new AuthenticationMechanismsHandler(handler, mechanisms)
        handler = new SessionAttachmentHandler(handler, new JeedupSessionManager(), new JeedupSessionConfig())
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler)
        return handler
    }

    public void shutdown() {
        server.stop()
    }
}
