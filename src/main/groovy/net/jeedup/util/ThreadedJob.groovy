package net.jeedup.util

import groovy.transform.CompileStatic

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ConcurrentHashMap

/**
 * User: zack
 * Date: 11/6/11
 *
 * A multi threaded queue
 */
@CompileStatic
class ThreadedJob<T>  {

    private int numThreads = 0

    private Closure callback = null

    private BlockingQueue<T> queue

    private List<Thread> threads = []

    private Map<T, T> currentWork = new ConcurrentHashMap<T, T>()

    /**
     * Create a threaded queue
     *
     * @param numThreads number of threads
     * @param jobT closure that accepts a single T arguement which is the work to act on
     */
    public ThreadedJob(int numThreads, Closure callback)    {
        this.numThreads = numThreads
        this.callback = callback

        this.queue = new LinkedBlockingQueue<T>(this.numThreads)

        for (int i = 0; i < this.numThreads; i++)   {
            addThread()
        }
    }

    private void addThread()    {
        this.threads << createThread()
    }

    private Thread createThread()   {
        return Thread.start    {
            while (true)    {
                T t = null
                try {
                    t = queue.take()
                } catch (InterruptedException ie)   {
                    return
                }

                currentWork.put(t, t)

                try {
                    callback.call(t)
                } catch (InterruptedException ie)   {
                    return
                } finally   {
                    currentWork.remove(t)
                }
            }
        }
    }

    private void shutdown() {
        this.threads*.interrupt()
    }

    public void add(T t)    {
        queue.put(t)
    }

    public void waitFor()   {
        while (queue.size() > 0 || currentWork.size() > 0)    {
            Thread.sleep(1000L * 5L)
        }

        shutdown()
    }
}
