package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.Undertow
import io.undertow.predicate.Predicates
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.encoding.ContentEncodingRepository
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.server.handlers.encoding.GzipEncodingProvider
import net.jeedup.persistence.sql.DataSources

@CompileStatic
class Jeedup {

    public Jeedup() {
    }

    public void start() {

        // configure datasources and data models
        DataSources.getInstance()

        Undertow server = Undertow.builder()
                .addListener(Config.port(), Config.host())
                .setHandler(createHttpHandler())
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

}
