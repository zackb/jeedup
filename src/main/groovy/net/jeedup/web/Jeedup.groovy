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
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.encoding.ContentEncodingRepository
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.server.handlers.encoding.GzipEncodingProvider
import net.jeedup.persistence.sql.DataSources
import net.jeedup.web.security.MapIdentityManager

@CompileStatic
class Jeedup {

    private Undertow server

    public Jeedup() { }

    public void start() {

        // configure datasources and data models
        DataSources.getInstance()

        server = Undertow.builder()
                .addHttpListener(Config.port(), Config.host())
                .setHandler(addSecurity(createHttpHandler()))
                .setWorkerThreads(400)
                .build()
        server.start()
    }

    private HttpHandler createHttpHandler() {
        final jeedupHandler = new JeedupHandler()

        final EncodingHandler handler = new EncodingHandler(new ContentEncodingRepository()
                .addEncodingHandler('gzip', new GzipEncodingProvider(), 50, Predicates.maxContentSize(5)))
                .setNext(jeedupHandler)

        return handler
    }

    public void shutdown() {
        server.stop()
    }


    private static final Map<String, char[]> users = [:]
    private static final IdentityManager identityManager

    static {
        users.put('zack', 'kaz1'.toCharArray())
        users.put('wea', 'wea1'.toCharArray())
        identityManager = new MapIdentityManager(users)
    }

    private static HttpHandler addSecurity(final HttpHandler toWrap) {
        HttpHandler handler = toWrap
        handler = new AuthenticationCallHandler(handler)
        handler = new AuthenticationConstraintHandler(handler)
        List<AuthenticationMechanism> mechanisms = Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism('Jeedup'))
        handler = new AuthenticationMechanismsHandler(handler, mechanisms)
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler)
        return handler
    }
}
