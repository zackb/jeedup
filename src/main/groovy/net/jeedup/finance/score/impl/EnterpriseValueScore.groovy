package net.jeedup.finance.score.impl

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.IScore
import net.jeedup.finance.score.Points

/**
 * Enterprise Value score
 */
@CompileStatic
class EnterpriseValueScore implements IScore {
    @Override
    public double score(Stock stock) {
        if (stock.enterpriseValue && stock.marketCapitalization)
            return stock.enterpriseValue - stock.marketCapitalization * Points.POINTS_ENTERPRISE_VALUE.value
        return 0
    }
}
