package net.jeedup.finance.score

import groovy.transform.CompileStatic
import net.jeedup.finance.StockEnricher
import net.jeedup.finance.UpdateFrequency
import net.jeedup.finance.model.Stock
import net.jeedup.finance.score.impl.*
import net.jeedup.util.ThreadedJob

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
        return UpdateFrequency.MILLISECOND
    }

    public static void main(String[] args) {
        ThreadedJob<Stock> job = new ThreadedJob<>(20, { Stock stock ->
            stock.score = score(stock)
            println 'SCORE: ' + stock.id + ' : ' + stock.score
            stock.save()
        })
        Stock.db().executeQuery('active = ?', [1]).each { Stock stock ->
            job.add(stock)
        }
        job.waitFor()
    }
}
