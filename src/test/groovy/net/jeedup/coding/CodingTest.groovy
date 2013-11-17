package net.jeedup.coding

import groovy.transform.CompileStatic
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import net.jeedup.web.http.TestJsonClass

/**
 * User: zack
 * Date: 11/16/13
 */
@CompileStatic
public class CodingTest extends TestCase {

    public CodingTest(String testName) {
        super(testName)
    }

    public static Test suite() {
        return new TestSuite(CodingTest.class)
    }

    public void testEndcodeDecodeMap() {
        Map data = ['foo':'Baz']
        byte[] encoded = BSON.encode(data)
        Map decoded = BSON.decode(encoded)
        assert decoded.size() == 1
        assert data['foo'] == 'Baz'
    }

    public void testEndcodeDecodeMapCompressed() {
        Map data = ['foo':'Baz', 'faz': 1234]
        byte[] encoded = BSON.encode(data, true)
        Map decoded = BSON.decode(encoded, true)
        assert decoded.size() == 2
        assert data['foo'] == 'Baz'
        assert data['faz'] == 1234
    }

    public void testEndcodeDecodeObject() {
        TestJsonClass test = new TestJsonClass()
        test.facebookReadScope = 'Thisisatest'
        test.socialSharingWatchTimeSecs = 2345
        test.minClientVersion = ['foo': 123434]
        byte[] encoded = BSON.encode(test)
        TestJsonClass result = BSON.decodeObject(encoded, TestJsonClass.class)
        assert result.facebookReadScope == 'Thisisatest'
        assert result.socialSharingWatchTimeSecs == 2345
        assert test.minClientVersion.size() == 1
        assert test.minClientVersion.foo == 123434
    }

    public void testEndcodeDecodeObjectCompressed() {
        TestJsonClass test = new TestJsonClass()
        test.facebookReadScope = 'Thisisatest'
        test.socialSharingWatchTimeSecs = 2345
        test.minClientVersion = ['foo': 123434]
        byte[] encoded = BSON.encode(test, true)
        TestJsonClass result = BSON.decodeObject(encoded, TestJsonClass.class, true)
        assert result.facebookReadScope == 'Thisisatest'
        assert result.socialSharingWatchTimeSecs == 2345
        assert test.minClientVersion.size() == 1
        assert test.minClientVersion.foo == 123434
    }
}
