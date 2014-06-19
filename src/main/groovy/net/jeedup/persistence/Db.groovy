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

    protected Class<T> clazz

    private static final Map<Class, DB<T>> dbs = [:]

    public abstract <T> void save(T obj)

    public abstract <T> void saveAll(List<T> objs)

    public abstract <T> T get(Object id)

    public abstract <T> List<T> getAll(List ids = null)

    protected abstract <T> void delete(T obj)

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

    public static final SqlDB<T> sql(Class clazz) {
        return (SqlDB<T>)db(clazz)
    }

    private static Map<Class, Map<String, Field>> fieldsCache = new ConcurrentHashMap<Class, Map<String, Field>>()

    protected final Map<String, Field> describeFields() {
        Map<String, Field> fields = fieldsCache.get(clazz)
        if (fields != null) {
            return fields
        }

        fields = new LinkedHashMap<String, Field>()

        for (Field field : clazz.declaredFields) {
            int no = Modifier.ABSTRACT | Modifier.STATIC
            if (!(field.modifiers & no) && (field.type.isPrimitive() || PERSISTABLE_TYPES.contains(field.type))) {
                field.accessible = true
                fields[field.name] = field
            }
        }

        fieldsCache.put(clazz, fields)
        return fields
    }

    protected <T> List values(T o) {
        List args = []
        describeFields().each { String name, Field field ->
            Object value = o ? field.get(o) : null
            if (name == 'lastUpdated') {
                args << new Date()
            } else if (name == 'dateCreated' && value == null) {
                args << new Date()
            } else {
                args << value
            }
        }
        return args
    }

    public <T> T instantiate(Map<String, Object> values = null) {
        Object obj = (T)clazz.newInstance()
        describeFields().each { String name, Field field ->
            field.set(obj, values[name])
        }

        return obj
    }

    protected static Set<Class> PERSISTABLE_TYPES = new HashSet<>([
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
