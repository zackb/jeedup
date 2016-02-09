package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Return on equity score
 */
@CompileStatic
class ReturnOnEquityScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.returnOnEquity ? stock.returnOnEquity * Points.POINTS_RETURN_ON_EQUITY.value : 0.0d
    }
}
