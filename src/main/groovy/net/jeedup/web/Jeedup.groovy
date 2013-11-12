package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.Undertow
import io.undertow.predicate.Predicates
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.encoding.ContentEncodingRepository
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.server.handlers.encoding.GzipEncodingProvider

@CompileStatic
class Jeedup {

    public Jeedup() {
    }

    public void start() {
        Undertow server = Undertow.builder()
                .addListener(8080, "localhost")
                .setHandler(createHttpHandler())
                .build()
        server.start()
    }

    private HttpHandler createHttpHandler() {
        final jeedupHandler = new JeedupHandler()

        final EncodingHandler handler = new EncodingHandler(new ContentEncodingRepository()
                .addEncodingHandler("gzip", new GzipEncodingProvider(), 50, Predicates.maxContentSize(5)))
                .setNext(jeedupHandler)

        return handler
    }

}
