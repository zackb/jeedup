package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.finance.StockService
import net.jeedup.finance.YahooAPI
import net.jeedup.finance.YahooCSV
import net.jeedup.finance.model.Analyst
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

    @Endpoint('f/suggest')
    def suggest(Map data) {
        String q = ((String)data.q)?.trim()
        //if (!q) return new Response().data(data).status(400)
        List<Stock> stocks = null
        if (q) {
            String like = q.replaceAll(' ', '%') + '%'
            stocks = Stock.db().executeQuery('id like ? or name like ? ', [like, like])
        } else {
            stocks = Stock.db().getAll()
        }

        List<Map<String, String>> symbs = new ArrayList<Map<String,String>>()
        for (Stock stock : stocks) {
            symbs.add([id: stock.id, name: stock.name])
        }

        JSON([data:symbs])
    }

    @Endpoint('f/lookup')
    def lookup(Map data) {

        String id = data.id?.toString()?.trim()
        if (!id) {
            throw new Exception('Missing required id')
        }
        Stock stock = Stock.get(id)
        if (!stock) {
            stock = new Stock(id:id)
        }

        if (!stock.lastUpdated || stock.lastUpdated.time < System.currentTimeMillis() - 1000 * 60 * 15)
            StockService.instance.enrich(stock)

        String cls = 'up green', plus = '+'
        if (stock.change < 0) {
            cls = 'down red'
            plus = ''
        }

        List<Analyst> analysts = Analyst.forSymbol(stock.id)
        def ass = [strong:[],buy:[],hold:[],under:[],sell:[]]
        boolean agood = false
        analysts.each { Analyst a ->
            agood = true
            ass.strong << a.strongBuy
            ass.buy << a.buy
            ass.hold << a.hold
            ass.under << a.underperform
            ass.sell << a.sell
        }
        HTML([
                stock:stock,
                lwr:stock.id.toLowerCase(),
                cls:cls,
                plus:plus,
                analys:agood ? ass : null,
                mcap: String.format("%.2fM", stock.marketCapitalization / 1000000.0),
                eval: String.format("%.2fM", stock.enterpriseValue / 1000000.0)
        ], 'admin/search')
    }

    @Endpoint('f/scan')
    def scan(Map data) {
        HTML([
            data: [
                [ name : 'Value',
                  stocks: StockService.instance.findValueStocks()
                ], [ name : 'Growth',
                  stocks: StockService.instance.findGrowthStocks()
                ], [ name: 'Dogs of the Dow',
                    stocks: StockService.instance.findDogsOfTheDow()
                ]
            ]
        ], 'admin/scan')
    }
}
