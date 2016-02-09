package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * 1yr Target price score
 */
@CompileStatic
class TargetPriceScore implements IScore {
    @Override
    public double score(Stock stock) {
        if (stock.oneyrTargetPrice && stock.ask)
            return (stock.oneyrTargetPrice - stock.ask) * Points.POINTS_TARGET_PRICE_CHANGE.value
        return 0d
    }
}
