package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * PE Ratio score
 */
@CompileStatic
class PEScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.peRatio ? stock.peRatio * Points.POINTS_PE.value : 0.0d
    }
}
