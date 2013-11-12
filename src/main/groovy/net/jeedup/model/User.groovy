package net.jeedup.model

import groovy.transform.CompileStatic
import net.jeedup.web.Model

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
@Model('mainDB')
class User {
    public Long id
    public String username
    public Date dateCreated
}
