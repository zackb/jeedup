package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.resource.FileResourceManager
import io.undertow.server.handlers.resource.ResourceHandler
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.Methods
import net.jeedup.reflection.ClassEnumerator
import net.jeedup.web.response.HTML
import net.jeedup.web.response.JSON

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class JeedupHandler implements HttpHandler {

    private Map<String, Route> routes = [:]
    private ResourceHandler resourceHandler
    private List<String> resources = ['css', 'images', 'js']

    public JeedupHandler() {
        registerHandlers('net.jeedup.web.handlers')
        createResourceHandler()
    }

    private void createResourceHandler() {

        String rootPath = getClass().getResource("/html").toString()
        if (rootPath.contains('!')) {
            rootPath = rootPath.substring(0, rootPath.lastIndexOf('!'))
        }
        rootPath = rootPath.substring(rootPath.indexOf("file:") + 5)

        rootPath = rootPath.substring(0, rootPath.lastIndexOf('/'))
        if (rootPath.endsWith('/build/resources/main')) {
            rootPath = rootPath.replace('/build/resources/main', '')
        }

        resourceHandler = new ResourceHandler()
                .setResourceManager(new FileResourceManager(new File(rootPath), 10485760))
                .setDirectoryListingEnabled(false)
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String path = exchange.getRequestPath()
        path = path.startsWith('/') ? path.substring(1) : path

        if (isResourceDir(path)) {
            resourceHandler.handleRequest(exchange)
            return
        }

        Route route = routes[path] ?: routes['404']
        exchange.startBlocking()
        Map requestData = parseRequestData(exchange)
        println 'Request: ' + path + ' ' + requestData
        Response response = route.invoke(requestData)

        if (response instanceof HTML) {
            HTML html = (HTML)response
            if (!html.view)
                html.view(route.path)
        }

        exchange.setResponseCode(response.status)
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, response.contentType)

        OutputStream outputStream = exchange.getOutputStream()
        response.render(outputStream)
        //exchange.endExchange()
    }

    private static Map parseRequestData(HttpServerExchange exchange) {
        Map result = [:]
        String contentType = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE)
        HttpString method = exchange.getRequestMethod()
        if (method == Methods.POST || method == Methods.PUT) {
            if (contentType?.contains('json')) {
                String json = ''
                exchange.getInputStream().newReader().eachLine { String line ->
                    json += line
                }
                result = JSON.parse(json)
            } else {
                exchange.getInputStream().newReader().eachLine { String line ->
                    result = line.split('&').collectEntries { String param ->
                        param.split('=').collect { String it -> URLDecoder.decode(it, 'UTF-8') }
                    }
                }
            }
        } else if (method == Methods.GET || method == Methods.DELETE) {
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

    private void registerHandlers(String packageName) {

        List<Class> classes = ClassEnumerator.getClassesForPackage(packageName)
        for (Class clazz : classes) {
            // no inner classes
            if (clazz.name.contains('$'))
                continue

            Object instance = clazz.newInstance()
            for (Method method : clazz.getMethods()) {
                Annotation annotation = method.getAnnotation(Endpoint)
                if (annotation) {
                    Route route = new Route()
                    route.path = annotation.value()
                    route.handler = instance
                    route.action = method
                    if (routes[route.path]) {
                        throw new Exception('The route: ' + route.path + ' is already registered')
                    }
                    routes[route.path] = route
                }
            }
        }
    }

    private boolean isResourceDir(String path) {

        return resources.contains(path.split('/')[0])
    }
}
