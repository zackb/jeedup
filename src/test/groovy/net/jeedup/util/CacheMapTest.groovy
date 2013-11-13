package net.jeedup.util

import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite

public class CacheMapTest extends TestCase {
    public CacheMapTest(String testName) {
        super(testName)
    }

    public static Test suite() {
        return new TestSuite(CacheMapTest.class)
    }

    public void testConcurrency() {
        CacheMap<String, Integer> map = new CacheMap<String, Integer>({ String key ->
            return key.toInteger()
        }, 200)

        List<Thread> threads =[]
        for (int i = 0; i < 100; i++) {
            threads << Thread.start {
                map.put("1", 1)
                assert map.get("1") == 1
                assert map.get("2") == 2
                Thread.sleep(100)
                //map.remove("1")
                assert map.get("1") == 1
                assert map.get("2") == 2
                map.remove("FFDS")
                assert map.size() == 2
            }
        }
        threads*.join()
    }
}
