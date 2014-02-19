package net.jeedup.message

import rx.Observable

/**
 * Created by zack on 2/18/14.
 */
interface MessageBroker<T> {
    public abstract void connect()
    public abstract void disconnect()
    public abstract Observable<T> observe()
    public abstract void sendMessage(T t)
    public abstract String getName()
}
