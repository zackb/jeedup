package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.finance.StockService
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
        List<Price> prices = YahooCSV.retieveHistoricalData((String)data.id)

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
}
