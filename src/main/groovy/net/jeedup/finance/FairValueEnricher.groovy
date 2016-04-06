package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import net.jeedup.net.http.Request

/**
 * Fair value calculator, for now use app data
 * Created by zack on 4/5/16.
 */
@CompileStatic
class FairValueEnricher implements StockEnricher {

    private static final List<String> EXCHANGES = ['NYQ', 'NMS', 'NGM', 'NCM', 'PCX']

    private static final String URL = 'http://www.enupgames.com/30SecondStockMarket/1.8/prod/United_States/%s.csv'

    @Override
    void enrich(Stock stock) {
        if (!EXCHANGES.contains(stock.stockExchange)) return

        println 'Calculating Fair Value for: ' + stock.id

        long ts = (System.currentTimeMillis() / 1000).longValue()
        Request request = new Request()
                .url(sprintf(URL, stock.id) + '?' + ts)
                .headers([
                    'Host':'www.enupgames.com',
                    'Accept': '*/*',
                    'User-Agent': '30SecondTraderFree/1.1 CFNetwork/758.3.15 Darwin/15.4.0',
                    'Accept-Language': 'en-us'
                ])

        try {
            String csv = HTTP.get(request)
            String[] parts = csv.split('\n')
            String fvline = parts[parts.length - 5]
            if (!fvline) return
            stock.fairValue = Double.parseDouble(
                                fvline.substring(
                                    fvline.indexOf('$') + 1,
                                    fvline.length() - 8))

            fvline = parts[parts.length - 2]
            stock.fairValueStr = fvline.substring(3, fvline.length() - 8)

            println stock.fairValueStr

            fvline = stock.fairValueStr
                            .replaceAll(',', '')
                            .replaceAll('%', '')

            stock.fairValuePercent = Double.parseDouble(
                                        fvline.substring(
                                            fvline.lastIndexOf(' ')))

            if (stock.fairValueStr.startsWith('Over')) stock.fairValuePercent *= -1.0d

        } catch (Exception e) {
            System.err.println('Failed getting fair value: ' + e.message)
        }
    }

    @Override
    void enrich(List<Stock> stocks) {
        stocks.each { try { enrich it } catch (Exception e) { e.printStackTrace() } }
    }

    @Override
    UpdateFrequency getUpdateFrequency() {
        return UpdateFrequency.MILLISECOND
    }

    public static void main(String[] args) {
        new FairValueEnricher().enrich([new Stock(id:'AAPL'), new Stock(id:'TSLA'), new Stock(id:'HPQ'), new Stock(id:'HPE')])
    }
}
