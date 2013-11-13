package net.jeedup.persistence.sql

import groovy.sql.Sql
import groovy.transform.CompileStatic
import net.jeedup.persistence.DB
import net.jeedup.persistence.Options

import javax.sql.DataSource
import java.lang.reflect.Field
import java.sql.Timestamp
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
        if (id(obj)) {
            Sql().executeInsert(describeInsertOrUpdateSql(), values(obj))
        } else {
            def res = Sql().executeInsert(describeInsertSql(), values(obj))
            setId(obj, res[0][0])
        }
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
        if (!row)
            return null
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

        Sql().eachRow(query, args ?: [], { row ->
            Object instance = clazz.newInstance()
            describeFields().each { String name, Field field ->
                field.set(instance, row[name])
            }
            results << instance
        })

        return results
    }

    /**
     * Very rudimentary create table syntax
     *
     */
    public void createTable(String engine = 'innodb') {
        String createSql = "create table `${clazz.simpleName}` ("
        Map<String, Field> fields = describeFields()
        Class idType = fields['id'].type
        if ([Long.class, Integer.class, int.class, long.class].contains(idType)) {
            createSql += '`id` int(10) unsigned not null auto_increment primary key,'
        } else if (idType == String.class) {
            createSql += '`id` varchar(255) character set utf8 not null primary key,'
        }
        List<String> constraints = []
        fields.each { String name, Field field ->
            Options options = field.getAnnotation(Options)
            if (name == 'id')
                return
            String datatype = ''
            switch (field.type) {
                case String:
                    datatype = "varchar(${options?.max() ?: 255}) character set utf8"
                    break
                case Long:
                    datatype = "bigint(${options?.max() ?: 10})"
                    break
                case Integer:
                case Short:
                    datatype = "int(${options?.max() ?: 10})"
                    break
                case Double:
                case Float:
                    datatype = 'double'
                    break
                case Date:
                case Timestamp:
                    datatype = 'datetime'
                    break
                case byte[]:
                    datatype = 'blob'
                    break
                default:
                    throw new Exception("Do not know how to handle: ${field.type.name}")
            }

            createSql += "`${name}` ${datatype},"

            if (options?.unique()) {
                createSql += " constraint u_${clazz.simpleName}_on_${field.name} unique(${field.name}),"
            }
            if (options?.index()) {
                createSql += " index idx_${clazz.simpleName}_on_${field.name} (${field.name}),"
            }
        }

        // trim last ,
        createSql = createSql.substring(0, createSql.length() - 1)

        createSql += ") engine=${engine} default charset=utf8"
        Sql().executeUpdate(createSql)
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

        List fields = describeFields().keySet().collect({ "`${it}`"})
        sql = "select ${fields.join(',')} from ${clazz.simpleName} where `id` = ?"
        selectSqlCache.put(clazz, sql)
        return sql
    }

    private static Map<Class, String> insertSqlCache = new ConcurrentHashMap<Class, String>()

    public String describeInsertSql() {
        String insertSql = insertSqlCache.get(clazz);
        if (insertSql) {
            return insertSql
        }

        insertSql = "insert into ${clazz.simpleName} ("

        def fields = describeFields()
        fields.keySet().each { String name ->
            insertSql += "`${name}`,"
        }
        // trim last ,
        insertSql = insertSql.substring(0, insertSql.length() - 1);

        insertSql += ") values (${questionMarksWithCommas(fields.size())})"

        insertSqlCache.put(clazz, insertSql)

        return insertSql
    }




    private static Map<Class, String> insertOrUpdateSqlCache = new ConcurrentHashMap<Class, String>()

    protected final String describeInsertOrUpdateSql() {

        String insertSql = insertOrUpdateSqlCache.get(clazz)

        if (insertSql) {
            return insertSql
        }

        Map<String, Field> fields = describeFields()

        String updateSql = ""
        insertSql = "insert into `${clazz.simpleName}` ("
        for (String name : fields.keySet()) {
            insertSql += "`${name}`,"
            updateSql += "`${name}` = values(`${name}`),"
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

        updateSql = "update `${clazz.simpleName}` set "

        describeFields().keySet().each { String name ->
            updateSql += "`${name}` = ?,"
        }

        // trim last ,
        updateSql = updateSql.substring(0, updateSql.length() - 1)

        updateSql += ' where `id` = ?'

        updateSqlCache.put(clazz, updateSql)

        return updateSql
    }

    protected Object id(T obj) {
        Field field = describeFields().get('id')
        return field.get(obj)
    }

    protected void setId(T obj, Object value) {
        Field field = describeFields().get('id')
        field.set(obj, value)
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