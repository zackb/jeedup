package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Created by zack on 3/17/15.
 */
@CompileStatic
class YahooBalanceSheetStockEnricher implements StockEnricher {
    @Override
    public void enrich(Stock stock) {
        println 'Adding Balance Sheet: ' + stock.id
        String url = 'http://finance.yahoo.com/q/bs?s=' + stock.id
        String html = HTTP.get(url)
        Document doc = Jsoup.parse(html)
        Elements tables = doc.select('table[cellpadding=2][width=100%][cellspacing=0][border=0]')
        if (!tables || tables.size() < 1) {
            return
        }

        Elements trs = tables.select('tr')
        Element currentAssets = trs.get(10)
        Element totalAssets = trs.get(18)
        Element currentLiabilities = trs.get(24)
        Element totalLiabilities = trs.get(30)
        stock.currentAssets = parseTableRowDouble(currentAssets)
        stock.totalAssets = parseTableRowDouble(totalAssets)
        stock.currentLiabilities = parseTableRowDouble(currentLiabilities)
        stock.totalLiabilities = parseTableRowDouble(totalLiabilities)
    }

    private Double parseTableRowDouble(Element tr) {
        //String text = tr.select('td').get(1).text()
        Elements tds = tr.select('td')
        if (tds.size() < 2) {
            return null
        }
        String text = tds.get(1).text()
        text = text.replaceAll(',', '')
        text = text.replaceAll("\u00A0",'')
        return text.isNumber() ? text.toDouble() * 1000 : null
    }

    @Override
    public void enrich(List<Stock> stocks) {
        stocks.each { try { enrich it } catch (Exception e) { e.printStackTrace() } }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.SECOND
    }

    public static void main(String[] args) {
        Stock stock = new Stock(id:'AAPL')
        new YahooBalanceSheetStockEnricher().enrich(stock)
        println stock.currentAssets
        println stock.totalAssets
        println stock.currentLiabilities
        println stock.totalLiabilities
    }
}
