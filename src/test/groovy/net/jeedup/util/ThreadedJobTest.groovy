package net.jeedup.util

import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite

/**
 * Created by zack on 6/2/14.
 */
class ThreadedJobTest extends TestCase {
    public ThreadedJobTest(String testName) {
        super(testName)
    }

    public static Test suite() {
        return new TestSuite(ThreadedJobTest.class)
    }

    public void testIt() {
        ThreadedJob<String> job = new ThreadedJob<String>(10, { String t ->
            assert t.toInteger() >= 0 && t.toInteger() <= 100
        })

        (0..100).each {
            try {
                job.add("" + it)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }

        job.waitFor()
    }
}
