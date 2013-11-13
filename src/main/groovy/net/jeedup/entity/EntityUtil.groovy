package net.jeedup.entity

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/13/13
 */
@CompileStatic
class EntityUtil {

    public static String uuid(IEntity entity) {
        return uuid(entity.entityTypeId, entity.entityId)
    }

    public static String uuid(int type, Object id) {
        return type + '-' + id
    }

    public static Object entityId(String uuid) {
        return uuidParts(uuid)[1]
    }

    public static int entityTypeId(String uuid) {
        return uuidParts(uuid)[0] as int
    }

    public static String[] uuidParts(String uuid) {
        return uuid.split('-')
    }
}
