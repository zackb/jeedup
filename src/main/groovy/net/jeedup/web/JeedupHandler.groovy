package net.jeedup.web

import groovy.transform.CompileStatic
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.resource.FileResourceManager
import io.undertow.server.handlers.resource.ResourceHandler
import io.undertow.util.HeaderMap
import io.undertow.util.HeaderValues
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.Methods
import net.jeedup.coding.JSON
import net.jeedup.net.http.Request
import net.jeedup.net.http.Response
import net.jeedup.reflect.ClassEnumerator
import net.jeedup.web.render.Render
import org.xnio.streams.ChannelInputStream

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

        /** uncomment and edit gradle build to include html in the jar
        String rootPath = getClass().getResource("/html").toString()
        if (rootPath.contains('!')) {
            rootPath = rootPath.substring(0, rootPath.lastIndexOf('!'))
        }
        rootPath = rootPath.substring(rootPath.indexOf("file:") + 5)

        rootPath = rootPath.substring(0, rootPath.lastIndexOf('/'))
        if (rootPath.endsWith('/build/resources/main')) {
            rootPath = rootPath.replace('/build/resources/main', '')
        }
        */
        String rootPath = '.'

        resourceHandler = new ResourceHandler()
                .setResourceManager(new FileResourceManager(new File(rootPath), 10485760))
                .setDirectoryListingEnabled(false)
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {

        if (exchange.isInIoThread()) {
            exchange.dispatch(this)
            return
        }

        String path = exchange.getRequestPath()
        path.trim()
        path = path.startsWith('/') ? path.substring(1) : path

        if (path.endsWith('/')) {
            path = path.substring(0, path.lastIndexOf('/'))
        }

        if (isResourceDir(path)) {
            resourceHandler.handleRequest(exchange)
            return
        }

        Route route = routes[path]
        Map requestData = [:]
        if (!route) {
            String[] parts = path.split('/')
            if (parts.length > 1) {
                String newPath = parts[0..parts.size() - 2].join('/')
                route = routes[newPath]
                if (route) {
                    requestData.id = parts[parts.size() - 1]
                }
            }
        }
        route = route ?: routes['404']
        parseRequestData(exchange, requestData)
        println 'Request: ' + path + ' ' + requestData
        Request request = new Request()
                            .data(requestData)
                            .url(route.path)
                            .headers(parseRequestHeaders(exchange))
        Response response = route.invoke(request)

        if (!response.view)
            response.view(route.path)

        exchange.setResponseCode(response.status)
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, response.contentType)

        ByteArrayOutputStream outs = new ByteArrayOutputStream()
        Render render = Render.forResponse(response)
        render.render(outs)
        exchange.responseSender.send(outs.toString())
    }

    private static Map<String, String> parseRequestHeaders(HttpServerExchange exchange) {
        HeaderMap map = exchange.getRequestHeaders()
        Map<String, String> headers = new HashMap<String, String>(map.size())
        for (HttpString name : map.getHeaderNames()) {
            HeaderValues values = map.get(name)
            String v = ''
            for (int i = 0; i < values.size(); i++) {
                v += values[i]
            }
            headers[name.toString()] = v
        }

        return headers
    }

    private static Map parseRequestData(HttpServerExchange exchange, Map result) {
        String contentType = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE)
        HttpString method = exchange.getRequestMethod()
        if (method == Methods.POST || method == Methods.PUT) {
            InputStream ins = new ChannelInputStream(exchange.getRequestChannel())
            if (contentType?.contains('json')) {
                String json = ''
                ins.newReader().eachLine { String line ->
                    json += line
                }
                result = JSON.decode(json)
            } else {
                ins.newReader().eachLine { String line ->
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
