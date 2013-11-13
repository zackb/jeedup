package net.jeedup.entity

import groovy.transform.CompileStatic
import net.jeedup.persistence.DB

/**
 * Add persistence methods to a class
 * Convenience class only. Not required.
 * User: zack
 * Date: 11/13/13
 *
 */
@CompileStatic
abstract class Entity<T> implements IEntity {

    private static Class<?> typeClass

    {
        if (!typeClass) {
            typeClass = getClass()
        }
    }

    public static <T> T get(Object id) {
        return db().get(id)
    }

    public static <T> DB<T> db() {
        return DB.db(typeClass)
    }

    public void save() {
        db().save(this)
    }

    public String uuid() {
        return EntityUtil.uuid(this)
    }
}
