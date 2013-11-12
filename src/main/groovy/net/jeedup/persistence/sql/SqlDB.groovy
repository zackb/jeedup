package net.jeedup.persistence.sql

import groovy.sql.Sql
import groovy.transform.CompileStatic
import net.jeedup.persistence.DB

import javax.sql.DataSource
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

/**
 * User: zack
 * Date: 11/12/13
 */
@CompileStatic
class SqlDB<T> extends DB<T> {

    private DataSource dataSource

    public SqlDB(Class clazz) {
        this.clazz = clazz
        this.dataSource = DataSources.forClass(clazz)
    }

    public <T> void save(T obj) {
        insertOrUpdate(obj)
    }

    public <T> void insertOrUpdate(T obj) {
        Sql().executeInsert(describeInsertSql(), values(obj))
    }

    public <T> void update(T obj) {
        List values = values(obj)
        values << id(obj)
        Sql().executeUpdate(describeUpdateSql(), values)
    }

    public <T> void saveAll(List<T> objs) {
        objs.each { T t ->
            save(t)
        }
    }

    public <T> T get(Object id) {
        def row = Sql().firstRow(describeSelectSql(), [id])
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

    protected Sql Sql() {
        return new Sql(dataSource)
    }


    private static Map<Class, String> selectSqlCache = new ConcurrentHashMap<Class, String>()

    protected final String describeSelectSql() {
        String sql = selectSqlCache.get(clazz)
        if (sql) {
            return sql
        }
        sql = "select ${describeFields().keySet().join(',')} from ${clazz.simpleName} where id = ?"
        selectSqlCache.put(clazz, sql)
        return sql
    }


    private static Map<Class, String> insertOrUpdateSqlCache = new ConcurrentHashMap<Class, String>()

    protected final String describeInsertSql() {

        String insertSql = insertOrUpdateSqlCache.get(clazz)

        if (insertSql) {
            return insertSql
        }

        Map<String, Field> fields = describeFields()

        String updateSql = ""
        insertSql = "insert into ${clazz.simpleName} ("
        for (String name : fields.keySet()) {
            insertSql += "${name},"
            updateSql += "${name} = values(${name}),"
        }
        // trim last ,
        insertSql = insertSql.substring(0, insertSql.length() - 1)
        updateSql = updateSql.substring(0, updateSql.length() - 1)

        insertSql += ") values (${questionMarksWithCommas(fields.size())})"

        insertSql += " on duplicate key update " + updateSql

        insertOrUpdateSqlCache.put(clazz, insertSql)
        return insertSql
    }

    private static Map<Class, String> updateSqlCache = new ConcurrentHashMap<Class, String>()

    private final String describeUpdateSql() {

        String updateSql = updateSqlCache.get(clazz)

        if (updateSql) {
            return updateSql
        }

        updateSql = "update ${clazz.simpleName} set "

        describeFields().keySet().each { String name ->
            updateSql += "${name} = ?,"
        }

        // trim last ,
        updateSql = updateSql.substring(0, updateSql.length() - 1)

        updateSql += ' where id = ?'

        updateSqlCache.put(clazz, updateSql)

        return updateSql
    }

    protected Object id(obj) {
        Field field = describeFields().get('id')
        return field.get(obj)
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
}
