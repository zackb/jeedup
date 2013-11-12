package net.jeedup.persistence.sql

import groovy.transform.CompileStatic
import net.jeedup.web.Config
import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
class DataSources {

    private static DataSources instance = null

    private static final Map<String, DataSource> dataSources

    static {
        dataSources = [:]
        Map configs = Config.getDataSources()
        configs.each { String name, Map c ->
            println "Name: ${name} Value: ${c}"
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

    public DataSources() {
    }

    public static DataSource getDefaultDataSource() {
        return dataSources.get('mainDB')
    }

    public static DataSources getInstance() {
        if (instance) {
            return instance
        }

        synchronized (DataSources.class) {
            if (instance == null) {
                instance = new DataSources()
            }
        }
        return instance
    }
}
