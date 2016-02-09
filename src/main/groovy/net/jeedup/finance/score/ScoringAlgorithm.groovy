package net.jeedup.finance.score

import groovy.transform.CompileStatic
import net.jeedup.finance.StockEnricher
import net.jeedup.finance.UpdateFrequency
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.impl.*

/**
 * Stock scoring algo
 */
@CompileStatic
class ScoringAlgorithm implements StockEnricher {

    private static final List<IScore> scorers = [
            new AnalystScore(),
            new CurrentRatioScore(),
            new EnterpriseValueScore(),
            new EPSScore(),
            new OperatingMarginScore(),
            new PEGScore(),
            new PEScore(),
            new ProfitMarginScore(),
            new ReturnOnAssetsScore(),
            new ReturnOnEquityScore(),
            new TargetPriceScore()
    ] as List<IScore>

    public static double score(Stock stock) {
        double score = 0
        for (IScore scorer : scorers) {
            double sc = scorer.score(stock)
            score += sc
        }
        return score
    }

    public static void main(String[] args) {
        Stock.db().executeQuery('active = ?', [1]).each { Stock stock ->
            try {
                double score = score(stock)
                stock.score = score
                println 'SCORE: ' + stock.id + ' : ' + score
                stock.save()
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    @Override
    public void enrich(Stock stock) {
        score stock
    }

    @Override
    public void enrich(List<Stock> stocks) {
        stocks.each { enrich it }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.SECOND
    }
}
