package net.jeedup.finance.score

import groovy.transform.CompileStatic

/**
 * Score algo points
 */
@CompileStatic
enum Points {
    POINTS_CURRENT_RATIO(10.0d),
    POINTS_PEG(-15.0d),
    POINTS_PE(5.0d),
    POINTS_ENTERPRISE_VALUE(5.0d),
    POINTS_EPS(15.0d),
    POINTS_TARGET_PRICE_CHANGE(20.0d),
    POINTS_RETURN_ON_ASSETS(10.0d),
    POINTS_RETURN_ON_EQUITY(10.0d),
    POINTS_PROFIT_MARGIN(5.0d),
    POINTS_OPERATING_MARGIN(5.0d),
    POINTS_ANALYST_STRONG_BUY(30.0d),
    POINTS_ANALYST_BUY(10.0d),
    POINTS_ANALYST_HOLD(1.0d),
    POINTS_ANALYST_UNDERPERFORM(20.0d),
    POINTS_ANALYST_SELL(40.0d)

    double value

    private Points(double v) {
        this.value = v
    }
}
