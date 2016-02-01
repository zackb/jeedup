package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.finance.StockService
import net.jeedup.finance.YahooAPI
import net.jeedup.finance.YahooCSV
import net.jeedup.finance.model.Price
import net.jeedup.finance.model.Stock
import net.jeedup.web.Endpoint
import static net.jeedup.net.http.Response.*

/**
 * REST interface to market data
 */
@CompileStatic
class FinanceHandler {

    @Endpoint('stock')
    def get(Map data) {
        Stock stock = Stock.get(data.id)
        if (stock.lastUpdated.time < System.currentTimeMillis() - 1000 * 60 * 15)
            StockService.instance.enrich(stock)
        JSON(stock)
    }

    @Endpoint('stock/history')
    def history(Map data) {
        Stock stock = Stock.get(data.id)
        if (stock && (!stock.lastUpdated || stock.lastUpdated.time < System.currentTimeMillis() - 1000 * 60 * 15))
            StockService.instance.enrich(stock)
        List<Price> prices = YahooCSV.retieveHistoricalData((String)data.id)
        if (stock && stock.open && stock.daysHigh && stock.daysLow && stock.volume) {
            prices.add(new Price(
                    symbol: stock.id,
                    date: stock.lastTradeDate ?: stock.lastUpdated,
                    open: stock.open,
                    high: stock.daysHigh,
                    low: stock.daysLow,
                    close: stock.lastTradePriceOnly,
                    volume: stock.volume?.intValue()
            ))
        }

        if (!data.callback) {
            return JSON(prices)
        }

        // highstock format
        List<List<Number>> formatted = (prices.collect { [it.date.time, it.open, it.high, it.low, it.close] } as List<List<Number>>)

        String result = "" + data.callback + "("
        result += JSON.encode(formatted)
        result += ");"

        return TEXT(result)
    }

    @Endpoint('stock/news/embed')
    def newsEmbed(Map data) {
        String id = data.id?.toString()?.trim()
        if (!id) {
            throw new Exception('Missing required id')
        }
        HTML([items:YahooAPI.retrieveNewsItems(id)], 'admin/stock_news_embed')
    }
}
