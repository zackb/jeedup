package net.jeedup.finance

import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Get morningstar HTML data
 */
class MorningstarStockHTMLEnricher implements StockEnricher {

    @Override
    public void enrich(Stock stock) {
        try {
            String url = 'http://quotes.morningstar.com/stock/c-company-profile?&t=' + stock.id
            String html = HTTP.get(url)
            Document doc = Jsoup.parse(html)
            Element elem = doc.select('.gr_text5').get(0)
            stock.description = elem.getElementsByTag('br').get(0).siblingNodes().get(3).toString().trim()
        } catch (Exception e) {
            println 'Failed getting company description: ' + stock.id
        }
    }

    @Override
    void enrich(List<Stock> stocks) {
        stocks.each { try { enrich it } catch (Exception e) { e.printStackTrace() } }
    }

    @Override
    public UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.YEAR
    }

    public static void main(String[] args) {
        println new MorningstarStockHTMLEnricher().enrich(new Stock(id:'AAPL'))
    }
}
