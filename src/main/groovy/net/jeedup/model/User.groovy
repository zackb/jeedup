package net.jeedup.model

import groovy.transform.CompileStatic
import net.jeedup.persistence.Options
import net.jeedup.web.Model

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
@Model('mainDB')
class User {

    public Long id

    @Options(unique = true, index = true, max = 64)
    public String username

    public String passwd

    public Date dateCreated
}
