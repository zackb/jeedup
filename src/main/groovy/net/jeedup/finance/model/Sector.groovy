package net.jeedup.finance.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.web.Model

/**
 * Created by zack on 5/29/14.
 */
@CompileStatic
@Model('mainDB')
class Sector {
    public Long id
    public String title
    public Date lastUpdated
    public Date dateCreated

    public static SqlDB<Sector> db() {
        return DB.sql(Sector)
    }

    public static Sector get(Object id) {
        return db().get(id)
    }

    public void save() {
        db().save(this)
    }
}
