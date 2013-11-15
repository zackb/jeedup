package net.jeedup.model

import groovy.transform.CompileStatic
import net.jeedup.entity.Entity
import net.jeedup.persistence.Constraints
import net.jeedup.web.Model

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
@Model('auxDB')
class User extends Entity<User> {

    public Long id

    @Constraints(unique = true, index = true, max = 64)
    public String username

    public String passwd

    public Date dateCreated

    // IEntity
    @Override
    int getEntityTypeId() {
        return 0
    }

    @Override
    Object getEntityId() {
        return id
    }

    @Override
    String getTitle() {
        return username
    }

    /*
    protected static Class<?> getTypeClass() {
        return User.class
    }
    */
}
