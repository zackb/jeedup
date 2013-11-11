package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.Undertow
import io.undertow.server.HttpHandler

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
        return new JeedupHandler()
    }

}
