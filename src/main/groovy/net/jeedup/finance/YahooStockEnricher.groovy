package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock

/**
 *
 */
@CompileStatic
class YahooStockEnricher implements StockEnricher {
    @Override
    public void enrich(Stock stock) {
        enrich([stock])
    }

    @Override
    public void enrich(List<Stock> stocks) {
        Map<String, Stock> map = [:]
        for (Stock stock : stocks) {
            map[stock.id] = stock;
        }

        List<Map<String, String>> yahooData = YahooCSV.fetchData(map.keySet())

        for (Map<String, String> data : yahooData) {
            String symbol = data['Symbol']
            Stock stock = map[symbol]
            if (stock == null) {
                println "No symbol for: " + symbol
                stock = stocks.find { it.id.startsWith(symbol) }
                if (!stock) {
                    continue
                }
                println "Replaced with: " + stock.id
            }
            fillStockWithYahooData(stock, data)
        }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.HOUR
    }

    private static void fillStockWithYahooData(Stock stock, Map<String, String> data) {
        println "Filling: ${data['Symbol']}"
        stock.ask = parseDouble(data['Ask'])
        stock.askRealtime = parseDouble(data['AskRealtime'])
        stock.averageDailyVolume = parseDouble(data['AverageDailyVolume'])
        stock.bid = parseDouble(data['Bid'])
        stock.bidRealtime = parseDouble(data['BidRealtime'])
        stock.bidSize = parseDouble(data['BidSize'])
        stock.bookValuePerShare = parseDouble(data['BookValuePerShare'])
        stock.change = parseDouble(data['Change'])
        stock.changeFromFiftydayMovingAverage = parseDouble(data['ChangeFromFiftydayMovingAverage'])
        stock.changeFromTwoHundreddayMovingAverage = parseDouble(data['ChangeFromTwoHundreddayMovingAverage'])
        stock.changeFromYearHigh = parseDouble(data['ChangeFromYearHigh'])
        stock.changeFromYearLow = parseDouble(data['ChangeFromYearLow'])
        stock.changeInPercent = parseDouble(data['ChangeInPercent'])
        stock.changeRealtime = parseDouble(data['ChangeRealtime'])
        stock.currency = data['Currency']
        stock.daysHigh = parseDouble(data['DaysHigh'])
        stock.daysLow = parseDouble(data['DaysLow'])
        // TODO stock.dividendPayDate; //May 15,
        stock.trailingAnnualDividendYield = parseDouble(data['TrailingAnnualDividendYield'])
        stock.trailingAnnualDividendYieldInPercent = parseDouble(data['TrailingAnnualDividendYieldInPercent'])
        stock.dilutedEPS = parseDouble(data['DilutedEPS'])
        stock.ebitda = parseDouble(data['EBITDA'])
        stock.epsEstimateCurrentYear = parseDouble(data['EPSEstimateCurrentYear'])
        stock.epsEstimateNextQuarter = parseDouble(data['EPSEstimateNextQuarter'])
        stock.epsEstimateNextYear = parseDouble(data['EPSEstimateNextYear'])
        // TODO stock.exDividendDate; //May  8,
        stock.fiftydayMovingAverage = parseDouble(data['FiftydayMovingAverage'])
        // TODO stock.lastTradeDate; //5/28/2014,
        stock.lastTradePriceOnly = parseDouble(data['LastTradePriceOnly'])
        stock.lastTradeSize = parseDouble(data['LastTradeSize'])
        // TODO stock.lastTradeTime; //1:52pm,
        stock.lowLimit = parseDouble(data['LowLimit'])
        stock.marketCapitalization = parseDouble(data['MarketCapitalization'])
        stock.marketCapRealtime = parseDouble(data['MarketCapRealtime'])
        stock.moreInfo = data['MoreInfo']
        stock.oneyrTargetPrice = parseDouble(data['OneyrTargetPrice'])
        stock.open = parseDouble(data['Open'])
        stock.orderBookRealtime = parseDouble(data['OrderBookRealtime'])
        stock.pegRatio = parseDouble(data['PEGRatio'])
        stock.peRatio = parseDouble(data['PERatio'])
        stock.peRatioRealtime = parseDouble(data['PERatioRealtime'])
        stock.percentChangeFromFiftydayMovingAverage = parseDouble(data['PercentChangeFromFiftydayMovingAverage'])
        stock.percentChangeFromTwoHundreddayMovingAverage = parseDouble(data['PercentChangeFromTwoHundreddayMovingAverage'])
        stock.changeInPercentFromYearHigh = parseDouble(data['ChangeInPercentFromYearHigh'])
        stock.percentChangeFromYearLow = parseDouble(data['PercentChangeFromYearLow'])
        stock.previousClose = parseDouble(data['PreviousClose'])
        stock.priceBook = parseDouble(data['PriceBook'])
        stock.priceEPSEstimateCurrentYear = parseDouble(data['PriceEPSEstimateCurrentYear'])
        stock.priceEPSEstimateNextYear = parseDouble(data['PriceEPSEstimateNextYear'])
        stock.pricePaid = parseDouble(data['PricePaid'])
        stock.priceSales = parseDouble(data['PriceSales'])
        stock.revenue = parseDouble(data['Revenue'])
        stock.sharesOwned = parseDouble(data['SharesOwned'])
        stock.sharesOutstanding = parseDouble(data['SharesOutstanding'])
        stock.shortRatio = parseDouble(data['ShortRatio'])
        stock.stockExchange = data['StockExchange']
        stock.twoHundreddayMovingAverage = parseDouble(data['TwoHundreddayMovingAverage'])
        stock.volume = parseDouble(data['Volume'])
        stock.yearHigh = parseDouble(data['YearHigh'])
        stock.yearLow = parseDouble(data['YearLow'])
        stock.dividendYield = parseDouble(data['DividendYield'])
    }

    public static Double parseDouble(String str) {
        if (str == null) {
            return null
        }

        str = str.trim()

        def invalid = ['', '-', 'N/A', '"N/A"', 'NaN']
        if (invalid.contains(str)) {
            return null
        }

        str = str.replace('%', '')

        double multiplier = 1
        if (str.endsWith('B')) {
            multiplier = 1000000000
            str = str.replace('B', '')
        } else if (str.endsWith('M')) {
            multiplier = 1000000
            str = str.replace('M', '')
        } else if (str.endsWith('K')) {
            multiplier = 1000
            str = str.replace('K', '')
        } else if (str.endsWith('T')) {
            multiplier = 1000000000000
            str = str.replace('T', '')
        }

        try {
            return Double.parseDouble(str) * multiplier
        } catch (NumberFormatException e) {
        }
        return null
    }
}
