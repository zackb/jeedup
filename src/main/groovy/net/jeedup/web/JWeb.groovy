package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.Methods
import net.jeedup.reflection.ClassEnumerator

import java.lang.annotation.Annotation
import java.lang.reflect.Method

@CompileStatic
class JWeb {

    private Map<String, Route> routes = [:]

    public JWeb() {
        registerHandlers('net.jeedup.web.handlers')
    }

    public void start() {
        Undertow server = Undertow.builder()
                .addListener(8080, "localhost")
                .setHandler(createHttpHandler())
                .build()
        server.start()
    }

    private HttpHandler createHttpHandler() {

        return new HttpHandler() {

            public void handleRequest(HttpServerExchange exchange) throws Exception {
                String path = exchange.getRequestPath()
                path = path.startsWith('/') ? path.substring(1) : path
                Route route = routes[path] ?: routes['404']
                exchange.startBlocking()
                Map requestData = parseRequestData(exchange)
                println('Path: ' + path)
                println 'Request Params: ' +requestData
                Response response = route.invoke(requestData)

                exchange.setResponseCode(response.status)
                exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, response.contentType)
                OutputStream out = exchange.getOutputStream()
                response.render(out)
                exchange.endExchange()
            }

            private Map parseRequestData(HttpServerExchange exchange) {
                Map result = [:]
                HttpString method = exchange.getRequestMethod()
                if (method == Methods.POST) {
                    exchange.getInputStream().newReader().eachLine { String line ->
                        result = line.split('&').collectEntries { String param ->
                            param.split('=').collect { String it -> URLDecoder.decode(it, 'UTF-8') }
                        }
                    }
                } else if (method == Methods.GET) {
                    exchange.getQueryParameters().each { String name, Deque<String> values ->
                        if (values.size() == 1) {
                            result[name] = URLDecoder.decode(values[0], 'UTF-8')
                        } else {
                            result[name] = values.collect { String it -> URLDecoder.decode(it, 'UTF-8') }
                        }
                    }
                }
                return result
            }
        }
    }

    private void registerHandlers(String packageName) {

        List<Class> classes = ClassEnumerator.getClassesForPackage(packageName)
        for (Class clazz : classes) {
            Object instance = clazz.newInstance()
            for (Method method : clazz.getMethods()) {
                Annotation annotation = method.getAnnotation(Endpoint)
                if (annotation) {
                    Route route = new Route()
                    route.path = annotation.value()
                    route.handler = instance
                    route.action = method
                    routes[route.path] = route
                }
            }
        }
    }
}
