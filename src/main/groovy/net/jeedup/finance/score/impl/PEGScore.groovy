package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * PEG Ratio score
 */
@CompileStatic
class PEGScore implements IScore {
    @Override
    public double score(Stock stock) {
        return stock.pegRatio ? stock.pegRatio * Points.POINTS_PEG.value : 0.0d
    }
}
