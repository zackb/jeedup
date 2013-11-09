package net.jeedup.web

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers

class JWeb {

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain")
                        //exchange.getResponseSender().send(exchange.getRequestPath());
                        //exchange.getResponseSender().send(exchange.getQueryString())
                        exchange.getResponseSender().send('Hello, World!')
                    }
                }).build()

        server.start()
    }
}
