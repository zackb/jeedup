package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import net.jeedup.util.DateTimeUtil

/**
 * CNBC JSON/JSONP/XML api
 */
@CompileStatic
class CnbcStockEntricher implements StockEnricher {

    @Override
    public void enrich(Stock stock) {
        enrich([stock])
    }

    @Override
    public void enrich(List<Stock> stocks) {
        if (!stocks) return
        Map<String, Stock> stockMap = [:]
        stocks.each { stockMap[it.id] = it }
        String syms = (stocks*.id).join('|')
        String url = 'http://quote.cnbc.com/quote-html-webservice/quote.htm?symbols=' + syms + '&exthrs=1&extMode=&fund=1&entitlement=0&skipcache=&extendedMask=1&partnerId=2&output=json'
        Map<String, Object> data = JSON.decode(HTTP.get(url))
        Map<String, Object> quickQuoteResult = (Map<String, Object>)data.QuickQuoteResult
        List<Map<String, Object>> quotes = (List<Map<String, Object>>)quickQuoteResult.QuickQuote
        quotes.each {
            String sym = it.symbol
            Map<String, Object> funds = (Map<String, Object>)it.FundamentalData
            if (funds) {
                if (funds.yrlodate)
                    stockMap[sym].yearLowDate = DateTimeUtil.parseDate((String) funds.yrlodate)
                if (funds.yrhidate)
                    stockMap[sym].yearHighDate = DateTimeUtil.parseDate((String) funds.yrhidate)
                String beta = (String) funds.beta
                if (beta)
                    stockMap[sym].beta = Double.parseDouble(beta)
            }
        }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.MINUTE
    }
}
