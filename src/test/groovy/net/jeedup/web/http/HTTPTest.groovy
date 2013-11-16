package net.jeedup.web.http

import groovy.transform.CompileStatic
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import net.jeedup.net.http.HTTP

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
}
