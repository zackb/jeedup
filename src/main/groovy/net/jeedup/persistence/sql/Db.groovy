package net.jeedup.persistence.sql

import groovy.sql.Sql
import groovy.transform.CompileStatic

import javax.sql.DataSource
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.sql.Timestamp
import java.util.concurrent.ConcurrentHashMap

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
class Db<T> {

    private Class clazz
    private DataSource dataSource

    private static final Map<Class, Db<T>> dbs = [:]
    public static Db<T> db(Class clazz) {
        Db<T> db = dbs[clazz]
        if (!db) {
            synchronized (clazz) {
                if (!db) {
                    db = new Db<T>()
                    db.clazz = clazz
                    db.dataSource = DataSources.forClass(clazz)
                }
            }
        }

        return db
    }

    public <T> void save(T obj) {

    }

    public <T> T get(Object id) throws Exception {
        def row = new Sql(dataSource).firstRow(describeSelectSql(), [id])
        Object instance = clazz.newInstance()
        describeFields().each { String name, Field field ->
            field.set(instance, row[name])
        }

        return (T) instance
    }

    public <T> List<T> getAll(List ids) {
        return executeQuery("select * from ${clazz.simpleName} where id in (${questionMarksWithCommas(ids.size())}) ", ids)
    }

    public <T> List<T> executeQuery(String query, List args = null) throws Exception {
        List<T> results = []
        query = query.toLowerCase().trim().replaceAll("\n|\t|\r", ' ')
        if (query.startsWith('from')) {
            query = 'select * ' + query
        }

        new Sql(dataSource).eachRow(query, args ?: [], { row ->
            Object instance = clazz.newInstance()
            describeFields().each { String name, Field field ->
                field.set(instance, row[name])
            }
            results << instance
        })

        return results
    }


    private static Map<Class, String> selectSqlCache = new ConcurrentHashMap<Class, String>()

    public final String describeSelectSql() {
        String sql = selectSqlCache.get(clazz)
        if (sql) {
            return sql
        }
        sql = "select ${describeFields().keySet().join(',')} from ${clazz.simpleName} where id = ?"
        selectSqlCache.put(clazz, sql)
        return sql
    }

    private static Map<Class, Map<String, Field>> fieldsCache = new ConcurrentHashMap<Class, Map<String, Field>>()

    private final Map<String, Field> describeFields() {
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

    private static String questionMarksWithCommas(int num) {
        if (num == 0) {
            return ''
        }

        StringBuffer s = new StringBuffer()
        for (int h = 0; h < num; h++) {
            s.append('?')
            if (h < num - 1) {
                s.append(',')
            }
        }
        return s.toString()
    }



    private static HashSet<Class> PERSISTABLE_TYPES = new HashSet<>([
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
