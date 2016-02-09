package net.jeedup.finance.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.Constraints
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.web.Model

/**
 * Created by zack on 5/27/14.
 */
@CompileStatic
@Model('mainDB')
public class Stock {
    public String id //symbol
    public String name
    public String description
    @Constraints(max = 1)
    public int active
    public Date lastUpdated

    public Double score

    public Long industryId
    public Long sectorId

    public Double ask //627.61,
    public Double askRealtime //627.61,
    public Double averageDailyVolume //9535790,
    public Double bid //627.48,
    public Double bidRealtime //627.48,
    public Double bidSize //100,
    public Double bookValuePerShare //139.46,
    public Double change //+1.935,
    public Double changeFromFiftydayMovingAverage //+55.241,
    public Double changeFromTwoHundreddayMovingAverage //+81.305,
    public Double changeFromYearHigh //+1.705,
    public Double changeFromYearLow //+238.695,
    public Double changeInPercent //+0.31%,
    public Double changeRealtime //+1.935,
    public String currency //USD,
    public Double daysHigh //629.83,
    public Double daysLow //623.78,
    public Date dividendPayDate //May 15,
    public Double trailingAnnualDividendYield //12.44,
    public Double trailingAnnualDividendYieldInPercent //1.99,
    public Double dilutedEPS //41.727,
    public Double ebitda //57.795B,
    public Double epsEstimateCurrentYear //44.09,
    public Double epsEstimateNextQuarter //9.31,
    public Double epsEstimateNextYear //47.87,
    public Date exDividendDate //May  8,
    public Double fiftydayMovingAverage //572.324,
    public Date lastTradeDate //5/28/2014,
    public Double lastTradePriceOnly //627.565,
    public Double lastTradeSize //100,
    public Double lastTradeTime //1:52pm,
    public Double lowLimit //-,
    public Double marketCapitalization //540.6B,
    public Double marketCapRealtime //N/A,
    public String moreInfo //cnsprmiIed,
    public Double oneyrTargetPrice //628.33,
    public Double open //626.09,
    public Double orderBookRealtime //N/A,
    public Double pegRatio //0.95,
    public Double peRatio //14.99,
    public Double peRatioRealtime //N/A,
    public Double percentChangeFromFiftydayMovingAverage //+9.65%,
    public Double percentChangeFromTwoHundreddayMovingAverage //+14.88%,
    public Double changeInPercentFromYearHigh //+0.27%,
    public Double percentChangeFromYearLow //+61.38%,
    public Double previousClose //625.63,
    public Double priceBook //4.49,
    public Double priceEPSEstimateCurrentYear //14.19,
    public Double priceEPSEstimateNextYear //13.07,
    public Double pricePaid //-,
    public Double priceSales //3.06,
    public Double revenue //176.0B,
    public Double sharesOwned //-,
    public Double sharesOutstanding //111111111,
    public Double shortRatio //1.80,
    public String stockExchange //NasdaqNM,
    public Double twoHundreddayMovingAverage //546.26,
    public Double volume //7851565,
    public Double yearHigh //625.86,
    public Double yearLow //388.87

    public Double debtEquity
    public Double currentAssets
    public Double currentLiabilities
    public Double eps
    public Double beta
    public Double currentRatio
    public Double quickRatio

    public Double totalAssets
    public Double totalLiabilities

    public Double enterpriseValue
    public Double enterpriseValueRevenue
    public Double enterpriseValueEbitda
    public Double profitMargin // percent
    public Double operatingMargin // percent
    public Double returnOnAssets // percent
    public Double returnOnEquity // percent
    public Double revenuePerShare
    public Double grossProfit
    public Double cash
    public Double cashPerShare
    public Double debt


    // Analysts
    public Double meanAnalystRating
    public Integer analystStrongBuy
    public Integer analystBuy
    public Integer analystHold
    public Integer analystUnderperform
    public Integer analystSell

    public Date yearHighDate
    public Date yearLowDate


    public static SqlDB<Stock> db() {
        return DB.sql(Stock)
    }

    public static Stock get(Object id) {
        return db().get(id)
    }

    public void save() {
        db().save(this)
    }

    @Override
    public String toString() {
        return id + ' - ' + name
    }

    /*
    select * from Stock
        where
            `pegRatio` < 1
        and
            `debtEquity` < 1
        and
            `currentAssets` >= `currentLiabilities`*2
        and
            peRatio is not null
        and
            `open` <= `bookValuePerShare`
        order by
            `peRatio` asc;
     */




    /*
    select
            s.id, s.ask, s.yearLow, s.yearHigh, s.pegRatio,
            s.debtEquity, s.peRatio, s.eps, a.strongBuy,
            a.buy, a.hold, a.underperform, a.sell, a.meanRating
        from
            Stock s, Analyst a
        where
            `pegRatio` < 1
        and
            `debtEquity` < 1
        and
            `currentAssets` >= `currentLiabilities`*2
        and
            peRatio is not null
        and
            `open` <= `bookValuePerShare`
        and
        	s.id = a.symbol
       	and
       		a.meanRating is not null
        order by
            `peRatio` asc;
     */
}
