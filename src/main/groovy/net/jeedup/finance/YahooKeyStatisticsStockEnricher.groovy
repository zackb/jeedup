package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import static net.jeedup.finance.YahooStockEnricher.parseDouble

/**
 * Created by zack on 3/17/15.
 */
@CompileStatic
class YahooKeyStatisticsStockEnricher implements StockEnricher {
    @Override
    public void enrich(Stock stock) {
        println 'Adding Key Statistics: ' + stock.id
        String url = 'http://finance.yahoo.com/q/ks?s=' + stock.id + '+Key+Statistics'
        String html = HTTP.get(url)
        Document doc = Jsoup.parse(html)
        Elements tables = doc.select('table[cellpadding=2][width=100%][cellspacing=1][border=0]')
        if (!tables || tables.size() < 1) {
            return
        }
        Element valuationMeasures = tables.get(0)
        stock.enterpriseValue = parseTableRowValue(valuationMeasures, 1)
        stock.enterpriseValueRevenue = parseTableRowValue(valuationMeasures, 7)
        stock.enterpriseValueEbitda = parseTableRowValue(valuationMeasures, 8)

        Element profitability = tables.get(2)
        stock.profitMargin = parseTableRowValue(profitability, 1)
        stock.operatingMargin = parseTableRowValue(profitability, 2)

        Element managementEffectiveness = tables.get(3)
        stock.returnOnAssets = parseTableRowValue(managementEffectiveness, 1)
        stock.returnOnEquity = parseTableRowValue(managementEffectiveness, 2)

        Element incomeStatement = tables.get(4)
        stock.revenuePerShare = parseTableRowValue(incomeStatement, 2)
        stock.grossProfit = parseTableRowValue(incomeStatement, 4)
        stock.eps = parseTableRowValue(incomeStatement, 7)

        Element balanceSheet = tables.get(5)
        stock.cash = parseTableRowValue(balanceSheet, 1)
        stock.cashPerShare = parseTableRowValue(balanceSheet, 2)
        stock.debt = parseTableRowValue(balanceSheet, 3)
        stock.debtEquity = parseTableRowValue(balanceSheet, 4)
        stock.currentRatio = parseTableRowValue(balanceSheet, 5)
    }

    @Override
    public void enrich(List<Stock> stocks) {
        stocks.each { try { enrich it } catch (Exception e) { e.printStackTrace() } }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.DAY
    }

    private Double parseTableRowValue(Element table, int row) {
        String text = table.select('tr').get(row).select('td').get(1).text()
        return parseDouble(text)
    }

    public static void main(String[] args) {
        Stock stock = Stock.db().get('FARM')
        new YahooKeyStatisticsStockEnricher().enrich(stock)
        println stock.enterpriseValue
        println stock.enterpriseValueRevenue
        println stock.enterpriseValueEbitda
        println stock.profitMargin
        println stock.operatingMargin
        println stock.returnOnAssets
        println stock.returnOnEquity
        println stock.revenuePerShare
        println stock.grossProfit
        println stock.cash
        println stock.debt
        println stock.eps
    }
}
