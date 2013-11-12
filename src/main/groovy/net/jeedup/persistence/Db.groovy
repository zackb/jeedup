package net.jeedup.persistence

import groovy.transform.CompileStatic
import net.jeedup.persistence.sql.SqlDB

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.sql.Timestamp
import java.util.concurrent.ConcurrentHashMap

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
abstract class DB<T> {

    protected Class clazz

    private static final Map<Class, DB<T>> dbs = [:]

    public abstract <T> void save(T obj);

    public abstract <T> void saveAll(List<T> objs);

    public abstract <T> T get(Object id);

    public abstract <T> List<T> getAll(List ids);

    public static final DB<T> db(Class clazz) {
        DB<T> db = dbs[clazz]
        if (!db) {
            synchronized (clazz) {
                if (!db) {
                    db = new SqlDB<T>(clazz)
                }
            }
        }

        return db
    }

    private static Map<Class, Map<String, Field>> fieldsCache = new ConcurrentHashMap<Class, Map<String, Field>>()

    public final Map<String, Field> describeFields() {
        Map<String, Field> fields = fieldsCache.get(clazz)
        if (fields != null) {
            return fields
        }

        fields = new LinkedHashMap<String, Field>()

        for (Field field : clazz.declaredFields) {
            int no = Modifier.ABSTRACT | Modifier.STATIC
            if (!(field.modifiers & no) && (field.type.isPrimitive() || PERSISTABLE_TYPES.contains(field.type))) {
                field.setAccessible(true)
                fields[field.name] = field
            }
        }

        fieldsCache.put(clazz, fields)
        return fields
    }

    protected static HashSet<Class> PERSISTABLE_TYPES = new HashSet<>([
            Boolean.class,
            Double.class,
            Float.class,
            Integer.class,
            Short.class,
            String.class,
            Long.class,
            Date.class,
            Timestamp.class,
            java.sql.Date.class,
            byte[].class
    ])

}
