package net.jeedup.finance.model

import groovy.transform.CompileStatic

/**
 * Created by zack on 1/28/16.
 */
@CompileStatic
class Price {
    String symbol
    Date date
    Double open
    Double high
    Double low
    Double close
    Integer volume
    Double adjClose
}
