package net.jeedup.web.http

import groovy.transform.CompileStatic
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import net.jeedup.net.http.HTTP
import net.jeedup.net.http.Request
import net.jeedup.net.http.Response

/**
 * User: zack
 * Date: 11/15/13
 */
@CompileStatic
public class HTTPTest extends TestCase {

    public HTTPTest(String testName) {
        super(testName)
    }

    public static Test suite() {
        return new TestSuite(HTTPTest.class)
    }

    public void testHttpString() {
        assert HTTP.get("http://zackbartel.com/ua.php", ['User-Agent':'Zack']).contains('[HTTP_USER_AGENT] => Zack')
    }

    public void testJsonMap() {
        String url = "https://api.frequency.com/api/1.0/info"
        Map json = HTTP.getJSON(url)
        assert json.containsKey('facebookReadScope')
    }

    public void testJsonObject() {
        String url = "https://api.frequency.com/api/1.0/info"
        TestJsonClass obj = HTTP.getJSONObject(url, TestJsonClass.class)
        assert obj.minClientVersion instanceof Map<String, Object>
        assert obj.socialSharingWatchTimeSecs instanceof Integer
        assert obj.facebookReadScope.contains('read_stream')
    }

    public void testPost() {
        String resp = HTTP.post('http://zackbartel.com/ua.php', ['foo':'Hello'])
        assert resp.contains('[CONTENT_TYPE] => application/x-www-form-urlencoded')
        assert resp.replaceAll('\n', ' ').contains('[_POST] => Array         (             [{foo] => Hello}')
    }

    public void testPostJson() {
        TestJsonClass test = new TestJsonClass()
        test.minClientVersion = ['mydevice': 123333]
        test.socialSharingWatchTimeSecs = 100
        test.facebookReadScope = 'NotAScope'
        String resp = HTTP.postJSON('http://zackbartel.com/ua.php', test)
        assert resp.contains('[CONTENT_TYPE] => application/json')
        assert resp.contains('[HTTP_RAW_POST_DATA] => {"facebookReadScope":"NotAScope","socialSharingWatchTimeSecs":100,"minClientVersion":{"mydevice":123333}}')
    }

    public void test404() {
        String url = 'http://www.example.com/doesnotexist'
        Request request = new Request().url(url)
        Response response = HTTP.performRequest(request)
        assert response.status == 404
    }
}
