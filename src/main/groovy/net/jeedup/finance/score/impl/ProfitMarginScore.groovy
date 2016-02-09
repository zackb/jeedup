package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Created by zack on 2/8/16.
 */
@CompileStatic
class ProfitMarginScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.profitMargin ? stock.profitMargin * Points.POINTS_PROFIT_MARGIN.value : 0.0d
    }
}
