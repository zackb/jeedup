package net.jeedup.persistence.sql

import groovy.transform.CompileStatic
import net.jeedup.entity.Entity
import net.jeedup.reflect.ClassEnumerator
import net.jeedup.web.Config
import net.jeedup.web.Model
import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource
import java.lang.annotation.Annotation

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
class DataSources {

    private static DataSources instance = null

    private final Map<String, DataSource> dataSources = [:]
    private final Map<Class, DataSource> models = [:]

    static {
        getInstance()
    }

    private DataSources() {
        config()
    }

    public static DataSources getInstance() {
        if (!instance) {
            synchronized (DataSources.class) {
                if (!instance) {
                    instance = new DataSources()
                }
            }
        }

        return instance
    }

    private void config() {
        registerDataSources()
        registerModels('net.jeedup.model')
    }

    public static DataSource forClass(Class clazz) {
        DataSource ds = getInstance().models[clazz]
        if (!ds) {
            // TODO: Clean this all up
            ds = getInstance().findDataSource(clazz)
            if (ds) getInstance().models[clazz] = ds
        }
        return ds
    }


    private void registerDataSources() {
        Map configs = Config.getDataSources()
        configs.each { String name, Map c ->
            BasicDataSource dataSource = new BasicDataSource()
            dataSource.driverClassName = c.driverClassName ?: 'com.mysql.jdbc.Driver' //'org.mariadb.jdbc.Driver'
            dataSource.url             = c.url
            dataSource.username        = c.username
            dataSource.password        = c.password
            dataSource.initialSize     = (int)c.initialSize ?: 10
            dataSource.maxActive       = (int)c.maxActive ?: 600
            dataSource.maxIdle         = (int)c.maxIdle ?: 10
            dataSource.testOnBorrow    = (boolean)c.testOnBorrow ?: false
            dataSource.testWhileIdle   = (boolean)c.testWhileIdle ?: true
            dataSources[name] = dataSource
        }
    }

    private void registerModels(String packageName) {
        List<Class> classes = ClassEnumerator.getClassesForPackage(packageName)
        for (Class<?> clazz : classes) {
            DataSource ds = findDataSource(clazz)
            if (ds) {
                models[clazz] = ds
            }
        }
    }

    private DataSource findDataSource(Class clazz) {
        DataSource ds = null
        // no inner classes
        if (clazz.name.contains('$')) {
            return null
        }

        Annotation annotation = clazz.getAnnotation(Model.class)
        if (annotation) {
            if (Entity.class.isAssignableFrom(clazz)) {
                // hack to initialize type information
                clazz.newInstance()
            }
            String dsName = annotation.value()
            ds = dataSources[dsName]
            if (!ds) {
                throw new IllegalArgumentException("No such datasource ${dsName} for model ${clazz.name}")
            }
        }
        return ds
    }
}
