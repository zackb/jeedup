package net.jeedup.model.finance;

import groovy.transform.CompileStatic
import net.jeedup.persistence.Constraints;
import net.jeedup.web.Model;

/**
 * Created by zack on 5/27/14.
 */
@CompileStatic
@Model('mainDB')
public class Stock {
    public String id; //symbol
    public String name;
    @Constraints(max = 1)
    public int active;
    public Date lastUpdated
}
