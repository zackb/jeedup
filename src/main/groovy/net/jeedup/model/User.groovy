package net.jeedup.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.Constraints
import net.jeedup.web.Model

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
@Model('auxDB')
class User {

    public Long id

    @Constraints(unique = true, index = true, max = 64)
    public String username

    public String passwd

    public Date dateCreated
}
