package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * EPS Score
 */
@CompileStatic
class EPSScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.eps ? stock.eps * Points.POINTS_EPS.value : 0.0d
    }
}
