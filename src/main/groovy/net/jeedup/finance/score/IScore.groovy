package net.jeedup.finance.score

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock

/**
 * Scoreable
 */
@CompileStatic
interface IScore {
    public double score(Stock stock)
}