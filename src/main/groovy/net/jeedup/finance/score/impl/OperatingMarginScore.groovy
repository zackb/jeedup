package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points


/**
 * Operating margin score
 */
@CompileStatic
class OperatingMarginScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.operatingMargin ? stock.operatingMargin * Points.POINTS_OPERATING_MARGIN.value : 0.0d
    }
}
