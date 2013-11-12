package net.jeedup.persistence.sql

import groovy.transform.CompileStatic
import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource

/**
 * User: zack
 * Date: 11/11/13
 */
@CompileStatic
class DataSources {

    private BasicDataSource dataSource

    private static DataSources instance = null

    public DataSources() {
        dataSource = new BasicDataSource()
        dataSource.driverClassName = 'com.mysql.jdbc.Driver'
        dataSource.url = 'jdbc:mysql://127.0.0.1:3306/frequency?relaxAutoCommit=true&autoReconnect=true'
        dataSource.username = 'root'
        dataSource.password = 'G3tB4ck'
        dataSource.initialSize = 10
        dataSource.maxActive = 600
        dataSource.maxIdle = 10
        dataSource.testOnBorrow = false
        dataSource.testWhileIdle = true
    }

    public static DataSource getDefaultDataSource() {
        return getInstance().dataSource
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
