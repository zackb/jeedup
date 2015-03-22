package net.jeedup.collect

import groovy.transform.CompileStatic

/**
 * Created by zack on 3/17/15.
 */
@CompileStatic
class Tuple<T, U> {
    public final T first
    public final U second

    public Tuple(T t, U u) {
        this.first = t
        this.second = u
    }
}
