package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Current ratio score
 */
@CompileStatic
class CurrentRatioScore implements IScore {
    @Override
    public double score(Stock stock) {

        double currentRatio = 0

        if (stock.currentRatio)
            currentRatio = stock.currentRatio

        if (!currentRatio && stock.currentAssets && stock.currentLiabilities)
            currentRatio = stock.currentAssets / stock.currentLiabilities

        return currentRatio * Points.POINTS_CURRENT_RATIO.value
    }
}
