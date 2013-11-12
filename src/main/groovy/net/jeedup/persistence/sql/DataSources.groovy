package net.jeedup.persistence.sql

import groovy.transform.CompileStatic
import net.jeedup.reflection.ClassEnumerator
import net.jeedup.web.Config
import net.jeedup.web.Model
import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource
import java.lang.annotation.Annotation
import java.lang.reflect.Method

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
        return getInstance().models[clazz]
    }


    private void registerDataSources() {
        Map configs = Config.getDataSources()
        configs.each { String name, Map c ->
            BasicDataSource dataSource = new BasicDataSource()
            dataSource.driverClassName = c.driverClassName ?: 'com.mysql.jdbc.Driver'
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
            // no inner classes
            if (clazz.name.contains('$'))
                continue

            Annotation annotation = clazz.getAnnotation(Model.class)
            if (annotation) {
                String dsName = annotation.value()
                DataSource ds = dataSources[dsName]
                if (!ds) {
                    throw new IllegalArgumentException("No such datasource ${dsName} for model ${clazz.name}")
                }
                models[clazz] = ds
            }
        }
    }
}
