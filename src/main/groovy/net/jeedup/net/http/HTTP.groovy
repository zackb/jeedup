package net.jeedup.net.http

import groovy.transform.CompileStatic
import net.jeedup.io.IOUtil

import java.nio.charset.Charset

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
                           'Connection':        'keep-alive'
        ]
    }

    public Response perform(Request request) {
        Response response = null
        return response
    }

    public static InputStream openInputStrem(Request request) {
        String urlStr = request.url
        if (request.data && request.data instanceof Map) {
            Map data = (Map<String, String>)request.data
            if (urlStr.contains('?'))
                urlStr += '&'
            else
                urlStr += '?'

            for (String name : data.keySet())
                urlStr += name + '=' + data[name]
        }

        URL url = new URL(urlStr)
        URLConnection conn = url.openConnection()

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
        InputStream ins = conn.getInputStream()
        if (request.method == 'POST') {
            OutputStream outs = conn.getOutputStream()
            IOUtil.copyStream(request.getInputStream(), outs)
        }

        return ins
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

    public static String post(String url, Map data = null, Map headers = null) {
        return null
    }
}
