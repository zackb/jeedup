package net.jeedup.net.http

import groovy.transform.CompileStatic
import net.jeedup.io.IOUtil

/**
 * User: zack
 * Date: 11/15/13
 */
@CompileStatic
class HTTP {

    private static final Map<String, String> DEFAULT_HEADERS

    static {
        //("Accept-Encoding", "gzip,deflate");
        DEFAULT_HEADERS = ['User-Agent':        'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36',
                           'Accept':            'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
                           'Accept-Language':   'en-us,en;q=0.8',
                           'Accept-Charset':    'utf-8,ISO-8859-1;q=0.7,*;q=0.7',
                           //'Content-Type':      'bin/bin',
                           'Connection':        'keep-alive'
        ]
    }

    public static InputStream openInputStrem(Request request) {
        Response response = performRequest(request)
        return response.inputStream
    }

    public static Response performRequest(Request request) {
        String urlStr = request.url
        if (request.method == 'GET' && request.data && request.data instanceof Map) {
            Map data = (Map<String, String>)request.data
            if (urlStr.contains('?'))
                urlStr += '&'
            else
                urlStr += '?'

            for (String name : data.keySet())
                urlStr += name + '=' + data[name]
        }

        URL url = new URL(urlStr)
        HttpURLConnection conn = (HttpURLConnection)url.openConnection()
        conn.requestMethod = request.method ?: 'GET'

        Map<String, String> hdrs = [:]
        hdrs.putAll(DEFAULT_HEADERS)
        if (request.headers) {
            hdrs.putAll(request.headers)
        }

        for (String name : hdrs.keySet()) {
            conn.setRequestProperty(name, hdrs[name])
        }

        conn.readTimeout = request.readTimeout
        conn.connectTimeout = request.connectTimeout
        if (request.method == 'POST' || request.method == 'PUT') {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            OutputStream outs = conn.getOutputStream()
            InputStream requestIns = request.getInputStream()
            IOUtil.copyStream(requestIns, outs)
            IOUtil.close(requestIns)
            IOUtil.close(outs)
        }

        Response response = null
        String contentType = conn.getHeaderField('Content-Type')

        if (contentType) {
            String contentTypeLower = contentType.toLowerCase()
            if (contentTypeLower.contains('json')) {
                response = Response.JSON()
            } else if (contentTypeLower.contains('html')) {
                response = Response.HTML()
            } else {
                response = Response.TEXT()
            }
        }

        response.contentType = contentType

        try {
            response.inputStream = conn.getInputStream()
        } catch (Exception e) {
            response.inputStream = conn.getErrorStream()
        }
        response.status = conn.getResponseCode()
        response.headers = [:]

        for (String name : conn.getHeaderFields()) {
            response.headers[name] = conn.getHeaderField(name)
        }

        return response
    }

    public static String get(Request request) {
        return IOUtil.readString(openInputStrem(request))
    }

    public static String get(String url, Map<String, String> headers = null) throws Exception {
        return get(new Request().url(url).headers(headers))
    }

    public static Map<String, Object> getJSON(String url) {
        return (Map)getJSONObject(url, Map.class)
    }

    public static <T> T getJSONObject(String url, Class<T> clazz) {
        Request request = new Request()
                .url(url)
                .headers(['Content-Type':'application/json'])
        return JSON.parseObject(get(request), clazz)
    }

    public static String post(String url, Object data = [:], Map headers = [:]) {
        Request request = new Request()
                 .url(url)
                 .headers(headers)
                 .data(data)
                 .method('POST')
        return IOUtil.readString(openInputStrem(request))
    }

    public static String postJSON(String url, Object data = [:], Map headers = [:]) {
        headers['Content-Type'] = 'application/json'
        return post(url, JSON.encode(data), headers)
    }
}
