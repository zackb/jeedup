package net.jeedup.finance

import groovy.transform.CompileStatic

/**
 *
 */
@CompileStatic
enum UpdateFrequency {
    SECOND(1000L),
    MINUTE(1000L * 60),
    HOUR(1000L * 60 * 60),
    DAY(1000L * 60 * 60 * 24),
    MONTH(1000L * 60 * 60 * 24 * 30),
    YEAR(1000L * 60 * 60 * 24 * 30 * 12)

    public final long value
    private UpdateFrequency(long value) {
        this.value = value
    }
}