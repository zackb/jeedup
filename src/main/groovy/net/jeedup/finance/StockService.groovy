package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.feed.FeedItem
import net.jeedup.feed.RssFeed
import net.jeedup.feed.parser.RssFeedParser
import net.jeedup.finance.model.Stock
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.util.ThreadedJob

/**
 * Stocks
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
        addEnrichers([
            new MorningstarStockEnricher(),
            new YahooStockEnricher(),
            new YahooAnalystsEnricher(),
            new YahooKeyStatisticsStockEnricher(),
            new YahooBalanceSheetStockEnricher(),
            new MorningstarStockHTMLEnricher(),
            new CnbcStockEntricher()
        ] as List<StockEnricher>)
    }

    public static StockService getInstance() {
        return instance
    }

    public void enrich(Stock stock) {
        for (StockEnricher enricher : enrichers)
            enricher.enrich([stock])
        stock.save()
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

    public List<Stock> findValueStocks() {
        return Stock.db().executeQuery("""
	pegRatio < 1.10
	and currentAssets > (currentLiabilities * 1.5)
	and  DATEDIFF(now(),lastUpdated) < 4
	and ask < yearLow + (yearHigh - yearLow)
	and stockExchange in ('NYQ', 'NMS', 'NGM', 'NCM')
	and priceBook > ask
	and oneyrTargetPrice > ask
	and enterpriseValue > marketCapitalization
    order by (oneyrTargetPrice - ask) desc limit 200""")

    }

    public List<Stock> findGrowthStocks() {
        return Stock.db().executeQuery("""
    epsEstimateNextYear > eps
    and oneyrTargetPrice > ask
	and  DATEDIFF(now(),lastUpdated) < 4
	and stockExchange in ('NYQ', 'NMS', 'NGM', 'NCM')
    order by (oneyrTargetPrice - ask) desc, returnOnEquity desc limit 100""")
    }

    public List<Stock> findDogsOfTheDow() {
        return Stock.db().executeQuery("""
	DATEDIFF(now(),lastUpdated) < 4
	and id in (${SqlDB.questionMarksWithCommas(DOW.size())})
    order by trailingAnnualDividendYieldInPercent desc limit 10""", DOW)
    }

    public List<Stock> findTopAnalyst() {
        return Stock.db().executeQuery("""
	DATEDIFF(now(),lastUpdated) < 4
	and active = 1
	and meanAnalystRating > 0
	and meanAnalystRating is not null
	and stockExchange in ('NYQ', 'NMS', 'NGM', 'NCM')
	order by analystStrongBuy desc, analystBuy desc, meanAnalystRating desc, analystSell asc, analystUnderperform asc limit 100
""")
    }
    public List<Stock> findPreCorrectionHighs() {
        return Stock.db().executeQuery("""
	DATEDIFF(now(),lastUpdated) < 4
	and active = 1
	and yearHighDate is not null
	and month(yearHighDate) in (9, 10)
	and stockExchange in ('NYQ', 'NMS', 'NGM', 'NCM')
	order by (yearHigh - ask) desc
""")
    }

    private static final List<String> DOW = [
        'MMM', 'AXP', 'AAPL', 'BA', 'CAT',
        'CVX', 'CSCO', 'KO', 'DIS', 'DD',
        'XOM', 'GE', 'GS', 'HD', 'IBM', 'INTC',
        'JNJ', 'JPM', 'MCD', 'MRK', 'MSFT', 'NKE',
        'PFE', 'PG', 'TRV', 'UTX', 'UNH', 'VZ', 'V', 'WMT'
    ]

    public List<FeedItem> retrieveNewsItems(String symbol) {
        List<FeedItem> result = []
        result.addAll(YahooAPI.retrieveNewsItems(symbol))
        String googleUrl = 'https://www.google.com/finance/company_news?q=' + symbol + '&output=rss'
        result.addAll(new RssFeedParser().parse(new RssFeed(url:googleUrl)))
        result.sort true, { FeedItem it -> -it.pubDate.time }
        return result
    }

    public static void main(String[] args) {
        /*
        getInstance().addEnrichers([new MorningstarStockEnricher(), new YahooStockEnricher(), new YahooAnalystsEnricher(), new YahooKeyStatisticsStockEnricher()] as List<StockEnricher>)
        getInstance().addEnricher(new YahooBalanceSheetStockEnricher())
        */
        println getInstance().enrichers
        getInstance().enrich()
    }
}
