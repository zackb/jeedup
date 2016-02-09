package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Analyst scores scores
 */
@CompileStatic
class AnalystScore implements IScore {
    @Override
    public double score(Stock stock) {
        double result = 0.0d
        if (stock.analystStrongBuy) result += stock.analystStrongBuy * Points.POINTS_ANALYST_STRONG_BUY.value
        if (stock.analystBuy) result += stock.analystBuy * Points.POINTS_ANALYST_BUY.value
        if (stock.analystHold) result += stock.analystHold * Points.POINTS_ANALYST_HOLD.value
        if (stock.analystUnderperform) result += stock.analystUnderperform * Points.POINTS_ANALYST_UNDERPERFORM.value
        if (stock.analystSell) result += stock.analystSell * Points.POINTS_ANALYST_SELL.value
        return result
    }
}
