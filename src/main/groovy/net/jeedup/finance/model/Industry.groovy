package net.jeedup.finance.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.Constraints
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.web.Model

/**
 * Created by zack on 5/29/14.
 */
@CompileStatic
@Model('mainDB')
class Industry {

    public Long id
    @Constraints(unique = true, max = 128)
    public String title // name
    public Long sectorId
    public Long yahooId
    public Double peRatio
    public Date lastUpdated
    public Date dateCreated

    public static SqlDB<Industry> db() {
        return DB.sql(Industry)
    }

    public static Industry get(Object id) {
        return db().get(id)
    }

    public void save() {
        db().save(this)
    }
}
