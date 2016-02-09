package net.jeedup.finance.score.impl

import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Return on assets score
 */
class ReturnOnAssetsScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.returnOnAssets ? stock.returnOnAssets * Points.POINTS_RETURN_ON_ASSETS.value : 0.0d
    }
}
