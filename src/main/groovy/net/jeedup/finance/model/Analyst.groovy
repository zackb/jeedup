package net.jeedup.finance.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.web.Model

/**
 * Created by zack on 3/13/15.
 */
@CompileStatic
@Model('mainDB')
class Analyst {

    String id
    String symbol
    Integer strongBuy
    Integer buy
    Integer hold
    Integer underperform
    Integer sell

    Double meanRating

    Date date
    Date lastUpdated

    public static SqlDB<Analyst> db() {
        return DB.sql(Analyst)
    }

    public static Analyst get(Object id) {
        return db().get(id)
    }

    public void save() {
        db().save(this)
    }

    public static List<Analyst> forSymbol(String id, int limit = 4) {
        return db().executeQuery('id like ? order by date desc limit ?', [id + '_%', limit]).reverse()
    }

}
