package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.coding.CSV
import net.jeedup.net.http.HTTP

/**
 * Created by zack on 5/28/14.
 */
@CompileStatic
class MorningstarAPI {
    //http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t=XNAS:TSLA&region=usa&culture=en-US&cur=USD&order=asc

    public static final String CurrentRatio = 'Current Ratio'
    public static final String QuickRatio = 'Quick Ratio'
    public static final String DebtEquity = 'Debt/Equity'
    public static final String CurrentAssets = 'Total Current Assets'
    public static final String CurrentLiabilities = 'Total Current Liabilities'
    public static final String EarningsPerShare = 'Earnings Per Share USD'

    public static List<Map<String, String>> fetchData(List<String> symbols) {
        List<Map<String, String>> results = []

        String url = "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t=%s&region=usa&culture=en-US&cur=USD&order=asc"
        return results
    }

    public static Map<String, Double> fetchMinimalData(String symbol) {
        Map<String, Double> data = [:]
        String url = "http://financials.morningstar.com/ajax/exportKR2CSV.html?&callback=?&t=%s&region=usa&culture=en-US&cur=USD&order=asc"

        String dataStr = HTTP.get(sprintf(url, symbol))
        String[] lines = dataStr.split('\n')
        for (String line : lines) {
            String[] csv = CSV.parse(line)
            if (csv.size() < 1) {
                continue
            }

            switch (csv[0]) {
                case CurrentRatio:
                    data[CurrentRatio] = parseDouble(csv[csv.size() - 1])
                    break
                case QuickRatio:
                    data[QuickRatio] = parseDouble(csv[csv.size() - 1])
                    break
                case DebtEquity:
                    data[DebtEquity] = parseDouble(csv[csv.size() - 1])
                    break
                case CurrentAssets:
                    Double d = parseDouble(csv[csv.size() - 1])
                    data[CurrentAssets] = d ? d * 1000000 : null
                    break
                case CurrentLiabilities:
                    Double d = parseDouble(csv[csv.size() - 1])
                    data[CurrentLiabilities] = d ? d * 1000000 : null
                    break
                case EarningsPerShare:
                    data[EarningsPerShare] = parseDouble(csv[csv.size() - 1])
                    break
            }
        }

        return data
    }

    private static Double parseDouble(String val) {
        if (!val || !val.trim() || !val.isNumber()) {
            return null
        }
        return val.toDouble()
    }

    public static void main(String[] args) {
        println fetchMinimalData('AAPL')
    }


    static String test = """
Growth Profitability and Financial Ratios for Apple Inc
Financials
,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,TTM
Revenue USD Mil,"8,279","13,931","19,315","24,006","32,479","42,905","65,225","108,249","156,508","170,910","173,992"
Gross Margin %,27.3,29.0,29.0,34.0,34.3,40.1,39.4,40.5,43.9,37.6,37.4
Operating Income USD Mil,326,"1,650","2,453","4,409","6,275","11,740","18,385","33,790","55,241","48,999","49,252"
Operating Margin %,3.9,11.8,12.7,18.4,19.3,27.4,28.2,31.2,35.3,28.7,28.3
Net Income USD Mil,276,"1,335","1,989","3,496","4,834","8,235","14,013","25,922","41,733","37,037","37,031"
Earnings Per Share USD,0.36,1.56,2.27,3.93,5.36,9.08,15.15,27.68,44.15,39.75,40.24
Dividends USD,,,,,,,,,2.65,11.40,11.80
Payout Ratio %,,,,,,,,,6.0,28.7,29.2
Shares Mil,775,857,878,889,902,907,925,937,945,932,920
Book Value Per Share USD,6.54,8.95,11.74,16.71,25.17,35.16,52.18,82.45,125.86,137.40,139.52
Operating Cash Flow USD Mil,934,"2,535","2,220","5,470","9,596","10,159","18,595","37,529","50,856","53,666","52,910"
Cap Spending USD Mil,-176,-260,-657,-986,"-1,199","-1,213","-2,121","-7,452","-9,402","-9,076","-8,665"
Free Cash Flow USD Mil,758,"2,275","1,563","4,484","8,397","8,946","16,474","30,077","41,454","44,590","44,245"
Free Cash Flow Per Share USD,0.98,2.66,1.78,5.04,9.31,9.86,17.82,32.11,43.85,47.86,
Working Capital USD Mil,"4,375","6,816","8,038","12,657","20,598","20,049","20,956","17,018","19,111","29,628",

Key Ratios -> Profitability
Margins % of Sales,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,TTM
Revenue,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00
COGS,72.71,70.98,71.02,66.03,65.69,59.86,60.62,59.52,56.13,62.38,62.59
Gross Margin,27.29,29.02,28.98,33.97,34.31,40.14,39.38,40.48,43.87,37.62,37.41
SG&A,17.16,13.34,12.60,12.34,11.58,9.67,8.46,7.02,6.42,6.34,6.35
R&D,5.91,3.83,3.69,3.26,3.41,3.11,2.73,2.24,2.16,2.62,2.76
Other,0.28,,,,,,,,,,
Operating Margin,3.94,11.84,12.70,18.37,19.32,27.36,28.19,31.22,35.30,28.67,28.31
Net Int Inc & Other,0.69,1.18,1.89,2.50,1.91,0.76,0.24,0.38,0.33,0.68,0.54
EBT Margin,4.63,13.03,14.59,20.86,21.23,28.12,28.42,31.60,35.63,29.35,28.85

Profitability,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,TTM
Tax Rate %,27.94,26.45,29.42,30.19,29.89,31.75,24.42,24.22,25.16,26.15,26.22
Net Margin %,3.33,9.58,10.30,14.56,14.88,19.19,21.48,23.95,26.67,21.67,21.42
Asset Turnover (Average),1.11,1.42,1.34,1.13,1.00,0.99,1.06,1.13,1.07,0.89,0.88
Return on Assets %,3.71,13.62,13.83,16.43,14.89,18.92,22.84,27.07,28.54,19.34,18.82
Financial Leverage (Average),1.59,1.55,1.72,1.74,1.88,1.50,1.57,1.52,1.49,1.68,1.71
Return on Equity %,5.94,21.29,22.80,28.52,27.19,31.27,35.28,41.67,42.84,30.64,29.50
Return on Invested Capital %,5.94,21.29,22.80,28.52,27.19,31.27,35.28,41.67,42.84,26.94,26.20
Interest Coverage,,,,,,,,,,369.79,168.56

Key Ratios -> Growth
,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,Latest Qtr
Revenue %
Year over Year,33.38,68.27,38.65,24.29,35.30,32.10,52.02,65.96,44.58,9.20,4.69
3-Year Average,15.57,34.37,45.99,42.60,32.60,30.48,39.54,49.37,53.94,37.86,
5-Year Average,6.18,11.78,29.21,33.12,39.23,38.97,36.17,41.16,45.49,39.39,
10-Year Average,-1.04,2.33,6.98,12.99,18.52,21.47,23.37,35.05,39.17,39.31,
Operating Income %
Year over Year,,406.14,48.67,79.74,42.32,87.09,56.60,83.79,63.48,-11.30,8.24
3-Year Average,,359.56,,138.25,56.09,68.52,60.96,75.28,67.57,38.65,
5-Year Average,-1.91,25.88,,203.93,,104.78,61.96,68.97,65.80,50.84,
10-Year Average,-4.60,9.21,,,37.44,41.73,42.78,,124.48,,
Net Income %
Year over Year,300.00,383.70,48.99,75.77,38.27,70.36,70.16,84.99,60.99,-11.25,
3-Year Average,,173.86,206.62,133.11,53.56,60.57,58.85,75.03,71.77,38.26,
5-Year Average,-14.41,11.18,,121.89,133.93,97.22,60.03,67.11,64.20,50.27,
10-Year Average,-1.16,12.15,,,31.65,29.92,33.39,,90.88,87.49,
EPS %
Year over Year,273.68,339.44,45.51,73.13,36.39,69.40,66.85,82.71,59.50,,15.16
3-Year Average,,158.80,188.03,122.87,50.90,58.74,56.80,72.85,69.41,,
5-Year Average,-17.02,7.43,,112.83,124.02,91.24,57.56,64.90,62.22,,
10-Year Average,-5.94,6.11,,,26.15,25.97,30.11,,85.81,,

Key Ratios -> Cash Flow
Cash Flow Ratios,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,TTM
Operating Cash Flow Growth % YOY,223.18,171.41,-12.43,146.40,75.43,5.87,83.04,101.82,35.51,5.53,
Free Cash Flow Growth % YOY,506.40,200.13,-31.30,186.88,87.27,6.54,84.15,82.57,37.83,7.57,
Cap Ex as a % of Sales,2.13,1.87,3.40,4.11,3.69,2.83,3.25,6.88,6.01,5.31,4.98
Free Cash Flow/Sales %,9.16,16.33,8.09,18.68,25.85,20.85,25.26,27.79,26.49,26.09,25.43
Free Cash Flow/Net Income,2.75,1.70,0.79,1.28,1.74,1.09,1.18,1.16,0.99,1.20,1.19

Key Ratios -> Financial Health
Balance Sheet Items (in %),2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,Latest Qtr
Cash & Short-Term Investments,67.88,71.52,58.76,60.70,61.89,49.40,34.08,22.30,16.54,19.59,20.07
Accounts Receivable,9.61,7.75,7.28,15.90,11.89,10.65,13.20,10.07,10.62,9.97,7.68
Inventory,1.25,1.43,1.57,1.37,1.29,0.96,1.40,0.67,0.45,0.85,0.89
Other Current Assets,8.89,8.48,16.72,8.66,12.60,5.43,6.76,5.62,5.14,4.99,5.60
Total Current Assets,87.64,89.17,84.33,86.62,87.66,66.43,55.44,38.66,32.75,35.40,34.25
Net PP&E,8.78,7.07,7.45,7.23,6.20,6.22,6.34,6.68,8.78,8.02,7.34
Intangibles,1.20,0.83,1.15,1.66,1.24,1.18,1.44,3.81,3.04,2.78,2.90
Other Long-Term Assets,2.37,2.93,7.07,4.49,4.89,26.17,36.78,50.85,55.43,53.80,55.51
Total Assets,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00
Accounts Payable,18.02,15.40,19.70,19.61,13.95,11.79,15.98,12.57,12.03,10.81,9.18
Short-Term Debt,,,,,,,,,,,
Taxes Payable,,,,,,0.91,0.28,0.98,0.87,0.58,1.05
Accrued Liabilities,15.27,4.99,6.92,4.97,9.40,7.20,2.12,6.97,4.76,2.52,3.28
Other Short-Term Liabilities,,9.77,10.99,12.11,12.26,4.32,9.18,3.52,4.23,7.19,7.47
Total Current Liabilities,33.29,30.16,37.61,36.69,35.61,24.22,27.56,24.04,21.89,21.09,20.98
Long-Term Debt,,,,,,,,,,8.19,8.23
Other Long-Term Liabilities,3.65,5.20,4.36,5.98,11.25,9.17,8.87,10.13,10.97,11.03,12.45
Total Liabilities,36.94,35.36,41.97,42.67,46.86,33.39,36.43,34.16,32.86,40.31,41.66
Total Stockholders' Equity,63.06,64.64,58.03,57.33,53.14,66.61,63.57,65.84,67.14,59.69,58.34
Total Liabilities & Equity,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00,100.00

Liquidity/Financial Health,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,Latest Qtr
Current Ratio,2.63,2.96,2.24,2.36,2.46,2.74,2.01,1.61,1.50,1.68,1.63
Quick Ratio,2.33,2.63,1.76,2.09,2.07,2.48,1.72,1.35,1.24,1.40,1.32
Financial Leverage,1.59,1.55,1.72,1.74,1.88,1.50,1.57,1.52,1.49,1.68,1.71
Debt/Equity,,,,,,,,,,0.14,0.14

Key Ratios -> Efficiency Ratios
Efficiency,2004-09,2005-09,2006-09,2007-09,2008-09,2009-09,2010-09,2011-09,2012-09,2013-09,TTM
Days Sales Outstanding,33.95,21.86,20.29,21.96,22.81,24.60,24.82,18.34,19.01,25.66,17.40
Days Inventory,4.76,4.91,5.79,7.09,7.31,6.85,6.95,5.18,3.26,4.37,5.13
Payables Period,78.97,59.62,68.77,96.25,89.74,79.02,81.31,75.48,74.39,74.54,56.46
Cash Conversion Cycle,-40.27,-32.84,-42.70,-67.19,-59.61,-47.58,-49.53,-51.96,-52.13,-44.50,-33.92
Receivables Turnover,10.75,16.69,17.99,16.62,16.00,14.84,14.71,19.90,19.20,14.22,20.98
Inventory Turnover,76.69,74.35,63.07,51.47,49.90,53.28,52.51,70.53,112.12,83.45,71.14
Fixed Assets Turnover,12.03,18.28,18.41,15.42,15.15,15.86,16.89,17.26,13.48,10.67,11.68
Asset Turnover,1.11,1.42,1.34,1.13,1.00,0.99,1.06,1.13,1.07,0.89,0.88
"""
}
