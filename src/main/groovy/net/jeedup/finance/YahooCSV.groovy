package net.jeedup.finance

import net.jeedup.coding.CSV
import net.jeedup.finance.model.Price
import net.jeedup.net.http.HTTP
import net.jeedup.util.DateTimeUtil


class YahooCSV {

    // Broken as of 3/12/15. The trailing 0s have to be removed
    public static List<Map<String, String>> fetchData(Collection<String> symbols) {
        String fields = 'sab2a2bb3' + /*b6*/ 'b4c1m7m5k4j5p2c6c4hgr1d0y0e0j4e7e9e8qm3d1l1' + /*k3*/ 't1l3j1j3int8oi5r5rr2m8m6k5j6pp6r6r7p1p5' + /*s6*/ 's1j2s7xd2m4vkj'
        String url = 'http://download.finance.yahoo.com/d/quotes.csv?s=' + symbols.join('+') + '&f=' + fields + '&e=.csv';
        if (symbols.contains('AAPL')) {
            println url
        }
        String csv = HTTP.get(url)
        return CSV.decode(data, csv)
    }

    static def data = [
            'Symbol', //   Symbol s0 CHANGED TO 's'
            //'AfterHoursChangeRealtime', //     After Hours Change (Realtime)  c8
            //'AnnualizedGain', //   Annualized Gain    g3
            'Ask', //  Ask    a0
            'AskRealtime', //  Ask (Realtime) b2
            //'AskSize', //  Ask Size   a5
            'AverageDailyVolume', //   Average Daily Volume   a2
            'Bid', //  Bid    b0
            'BidRealtime', //  Bid (Realtime) b3
            //'BidSize', //  Bid Size   b6
            'BookValuePerShare', //    Book Value Per Share   b4
            'Change', //   Change c1
            //'Change_ChangeInPercent', //   Change Change In Percent   c0
            'ChangeFromFiftydayMovingAverage', //  Change From Fiftyday Moving Average    m7
            'ChangeFromTwoHundreddayMovingAverage', //     Change From Two Hundredday Moving Average  m5
            'ChangeFromYearHigh', //   Change From Year High  k4
            'ChangeFromYearLow', //    Change From Year Low   j5
            'ChangeInPercent', //  Change In Percent  p2
            //'ChangeInPercentRealtime', //  Change In Percent (Realtime)   k2
            'ChangeRealtime', //   Change (Realtime)  c6
            //'Commission', //   Commission c3
            'Currency', //     Currency   c4
            'DaysHigh', //     Days High  h0
            'DaysLow', //  Days Low   g0
            //'DaysRange', //    Days Range m0
            //'DaysRangeRealtime', //    Days Range (Realtime)  m2
            //'DaysValueChange', //  Days Value Change  w1
            //'DaysValueChangeRealtime', //  Days Value Change (Realtime)   w4
            'DividendPayDate', //  Dividend Pay Date  r1
            'TrailingAnnualDividendYield', //  Trailing Annual Dividend Yield d0
            'TrailingAnnualDividendYieldInPercent', //     Trailing Annual Dividend Yield In Percent  y0
            'DilutedEPS', //   Diluted E P S  e0
            'EBITDA', //   E B I T D A    j4
            'EPSEstimateCurrentYear', //   E P S Estimate Current Year    e7
            'EPSEstimateNextQuarter', //   E P S Estimate Next Quarter    e9
            'EPSEstimateNextYear', //  E P S Estimate Next Year   e8
            'ExDividendDate', //   Ex Dividend Date   q0
            'FiftydayMovingAverage', //    Fiftyday Moving Average    m3
            //'SharesFloat', //  Shares Float   f6
            //'HighLimit', //    High Limit l2
            //'HoldingsGain', //     Holdings Gain  g4
            //'HoldingsGainPercent', //  Holdings Gain Percent  g1
            //'HoldingsGainPercentRealtime', //  Holdings Gain Percent (Realtime)   g5
            //'HoldingsGainRealtime', //     Holdings Gain (Realtime)   g6
            //'HoldingsValue', //    Holdings Value v1
            //'HoldingsValueRealtime', //    Holdings Value (Realtime)  v7
            'LastTradeDate', //    Last Trade Date    d1
            'LastTradePriceOnly', //   Last Trade Price Only  l1
            //'LastTradeRealtimeWithTime', //    Last Trade (Realtime) With Time    k1
            //'LastTradeSize', //    Last Trade Size    k3
            'LastTradeTime', //    Last Trade Time    t1
            //'LastTradeWithTime', //    Last Trade With Time   l0
            'LowLimit', //     Low Limit  l3
            'MarketCapitalization', //     Market Capitalization  j1
            'MarketCapRealtime', //    Market Cap (Realtime)  j3
            'MoreInfo', //     More Info  i0
            'Name', //     Name   n0
            //'Notes', //    Notes  n4
            'OneyrTargetPrice', //     Oneyr Target Price t8
            'Open', //     Open   o0
            'OrderBookRealtime', //    Order Book (Realtime)  i5
            'PEGRatio', //     P E G Ratio    r5
            'PERatio', //  P E Ratio  r0
            'PERatioRealtime', //  P E Ratio (Realtime)   r2
            'PercentChangeFromFiftydayMovingAverage', //   Percent Change From Fiftyday Moving Average    m8
            'PercentChangeFromTwoHundreddayMovingAverage', //  Percent Change From Two Hundredday Moving Average  m6
            'ChangeInPercentFromYearHigh', //  Change In Percent From Year High   k5
            'PercentChangeFromYearLow', //     Percent Change From Year Low   j6
            'PreviousClose', //    Previous Close p0
            'PriceBook', //    Price Book p6
            'PriceEPSEstimateCurrentYear', //  Price E P S Estimate Current Year  r6
            'PriceEPSEstimateNextYear', //     Price E P S Estimate Next Year r7
            'PricePaid', //    Price Paid p1
            'PriceSales', //   Price Sales    p5
            //'Revenue', //  Revenue    s6
            'SharesOwned', //  Shares Owned   s1
            'SharesOutstanding', //    Shares Outstanding j2
            'ShortRatio', //   Short Ratio    s7
            'StockExchange', //    Stock Exchange x0
            //'TickerTrend', //  Ticker Trend   t7
            'TradeDate', //    Trade Date d2
            //'TradeLinks', //   Trade Links    t6
            //'TradeLinksAdditional', //     Trade Links Additional f0
            'TwoHundreddayMovingAverage', //   Two Hundredday Moving Average  m4
            'Volume', //   Volume v0
            'YearHigh', //     Year High  k0
            'YearLow', //  Year Low   j0
            //'YearRange' //    Year Range w0

    ]


    private static final List<String> histHeader = ['Date', 'Open', 'High', 'Low', 'Close', 'Volume', 'Adj Close']

    public static List<Price> retieveHistoricalData(String symbol) {
        String url = 'http://ichart.finance.yahoo.com/table.csv?s=' + symbol

        List<Price> result = []

        String csvs = HTTP.get(url)
        csvs = csvs.substring(csvs.indexOf('\n') + 1)

        List<Map<String, String>> csv =  CSV.decode(histHeader, csvs)

        for (Map<String, String> d : csv) {
            Price price = new Price(symbol:symbol)
            price.date = DateTimeUtil.parseDate(d.Date)
            price.open = Double.parseDouble(d.Open)
            price.high = Double.parseDouble(d.High)
            price.low = Double.parseDouble(d.Low)
            price.close = Double.parseDouble(d.Close)
            price.volume = Integer.parseInt(d.Volume)
            price.adjClose = Double.parseDouble(d.'Adj Close')
            result << price
        }

        result.sort true, { it.date.time }

        return result
    }
}
