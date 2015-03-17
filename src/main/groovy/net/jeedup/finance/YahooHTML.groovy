package net.jeedup.finance

import net.jeedup.model.finance.Analyst
import net.jeedup.model.finance.Stock
import net.jeedup.net.http.HTTP
import net.jeedup.util.ThreadedJob
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


/**
 * Quote: http://finance.yahoo.com/q?s=AAPL
 * Key Statistics: http://finance.yahoo.com/q/ks?s=AAPL+Key+Statistics
 * Analyst Opinion: http://finance.yahoo.com/q/ao?s=AAPL+Analyst+Opinion
 */
class YahooHTML {

    public static Collection<Analyst> fetchAnalysis(String symbol) {
        String url = 'http://finance.yahoo.com/q/ao?s=' + symbol + '+Analyst+Opinion'
        String html = HTTP.get(url)
        Document doc = Jsoup.parse(html)


        Element table = doc.select('table[cellpadding=0].yfnc_datamodoutline1').get(0).select('table[cellpadding=2]').get(0)
        Elements trs = table.select('tr')
        List<Integer> strongBuys = parseValues(trs.get(1))
        List<Integer> buys = parseValues(trs.get(2))
        List<Integer> holds = parseValues(trs.get(3))
        List<Integer> underperforms = parseValues(trs.get(4))
        List<Integer> sells = parseValues(trs.get(5))

        Analyst current = fill(symbol, 0, strongBuys, buys, holds, underperforms, sells)
        Analyst lastMonth = fill(symbol, 1, strongBuys, buys, holds, underperforms, sells)
        Analyst twoMonth = fill(symbol, 2, strongBuys, buys, holds, underperforms, sells)
        Analyst threeMonth = fill(symbol, 3, strongBuys, buys, holds, underperforms, sells)

        current.symbol = lastMonth.symbol = twoMonth.symbol = threeMonth.symbol = symbol

        current.meanRating = doc.select('table[cellpadding=2].yfnc_datamodoutline1').get(0).select('tr > td').get(1).text().toDouble()

        // TODO: saveUpdate for mean rating
        return [current, lastMonth, twoMonth, threeMonth]
    }

    private static Analyst fill(String symbol, int ago, List<Integer> strongBuys, List<Integer> buys, List<Integer> holds, List<Integer> underperforms, List<Integer> sells) {
        Analyst analyst = monthsAgo(symbol, ago)
        analyst.strongBuy = strongBuys[ago]
        analyst.buy = buys[ago]
        analyst.hold = holds[ago]
        analyst.underperform = underperforms[ago]
        analyst.sell = sells[ago]
        return analyst
    }

    private static Analyst monthsAgo(String symbol, int ago) {
        Calendar calendar = Calendar.getInstance()
        if (ago > 0) {
            calendar.add(Calendar.MONTH, -ago)
        }
        calendar.set(Calendar.DAY_OF_MONTH, 0)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DATE, 1)
        Date date = calendar.getTime()

        Analyst analyst = new Analyst()
        analyst.symbol = symbol
        analyst.date = date
        analyst.id = analyst.symbol + '_' + calendar.get(Calendar.YEAR)+ '_'+ calendar.get(Calendar.MONTH)
        return analyst
    }

    private static List<Integer> parseValues(Element tr) {
        List<Integer> values = []
        tr.select('td').each {
            values << it.text().toInteger()
        }
        return values
    }

    public static void main(String[] args) {
        ThreadedJob<Stock> job = new ThreadedJob<>(20, { Stock stock ->
            // TODO: dont overwrite old entries
            try {
                fetchAnalysis(stock.id).each {
                    it.save()
                }
            } catch (Exception e) {
                println 'Failed parsing ' + stock.id + ': ' +  e.message
            }
        })

        List<Stock> stocks = Stock.db().executeQuery('id not in (select symbol from Analyst)')
        stocks.each { Stock stock ->
            job.add(stock)
        }

        job.waitFor()
    }
}
