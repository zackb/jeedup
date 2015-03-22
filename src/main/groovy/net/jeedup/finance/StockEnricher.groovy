package net.jeedup.finance

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock

/**
 * Created by zack on 3/17/15.
 */
@CompileStatic
interface StockEnricher {
    public void enrich(Stock stock)
    public void enrich(List<Stock> stocks)
    public UpdateFrequency getUpdateFrequency()
}
