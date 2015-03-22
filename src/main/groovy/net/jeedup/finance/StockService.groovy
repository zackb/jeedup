package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.util.ThreadedJob

/**
 * Created by zack on 5/28/14.
 */
@CompileStatic
class StockService {

    private static StockService instance = new StockService()

    private final Set<StockEnricher> enrichers =
            new TreeSet<StockEnricher>(new Comparator<StockEnricher>() {
                @Override
                public int compare(StockEnricher s1, StockEnricher s2) {
                    return s2.updateFrequency.compareTo(s1.updateFrequency) // reverse the compare. Oldest first
                }
            })

    private boolean enriching = false

    private StockService() {
        super()
    }

    public static StockService getInstance() {
        return instance
    }

    public void enrich() {
        if (enriching) {
            throw new Exception('Enrich job already in progress')
        }
        enriching = true
        retrieveAndUpdateStockData()
        enriching = false
    }

    private void addEnrichers(Collection<StockEnricher> newEnrichers) {
        synchronized (enrichers) {
            enrichers.addAll(newEnrichers)
        }
    }

    private void addEnricher(StockEnricher enricher) {
        synchronized (enrichers) {
            enrichers.add(enricher)
        }
    }

    private void removeEnricher(StockEnricher enricher) {
        synchronized (enrichers) {
            enrichers.remove(enricher)
        }
    }

    public void updateSectorAndIndustryData() {
        YahooAPI.retrieveAllSectorsAndIndustries()
    }

    public void updateSecuritiesData() {
        YahooAPI.retrieveAllSecurities()
    }

    public void retrieveAndUpdateStockData() {

        ThreadedJob<List<Stock>> job = new ThreadedJob<List<Stock>>(20, { List<Stock> stocks ->
            for (StockEnricher enricher : enrichers) {
                enricher.enrich(stocks.findAll {
                    it.lastUpdated.time < (System.currentTimeMillis() - enricher.updateFrequency.value)
                } as List<Stock>)

            }
            for (Stock stock : stocks) {
                try {
                    stock.save()
                } catch (Exception e) {
                    System.err.println('Failed saving stock: ' + e.message)
                }
            }
        })

        List<Stock> stocks = Stock.db().executeQuery("select * from Stock where active = 1 order by id asc limit 40")
        while (stocks) {
            job.add(stocks)
            stocks = Stock.db().executeQuery("select * from Stock where active = 1 and id > ? order by id asc limit 40", [stocks.last().id])
        }

        job.waitFor()
    }

    public static void main(String[] args) {
        //getInstance().addEnrichers([new MorningstarStockEnricher(), new YahooStockEnricher(), new YahooAnalystsEnricher(), new YahooKeyStatisticsStockEnricher()])
        getInstance().addEnricher(new YahooBalanceSheetStockEnricher())
        println getInstance().enrichers
        getInstance().enrich()
    }
}
