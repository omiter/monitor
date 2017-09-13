package com.gome.monitor.datasource.mysql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by hutao on 2017/7/19.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.metadata",ignoreUnknownFields = false)
@Data
public class MetaDataDataSource {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int maxActive;
    private int maxIdle;
    private int minIdle;
    private int initialSize;
    private int maxWait;
    private int removeAbandonedTimeout;
    private boolean removeAbandoned;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private String validationQuery;
    private int timeBetweenEvictionRunsMillis;

    @Bean(name = "metaDataSource")
    public DataSource metaDataDataSource(){
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize); // 连接池启动时创建的初始化连接数量（默认值为0）
        dataSource.setMaxActive(maxActive); // 连接池中可同时连接的最大的连接数
        dataSource.setMaxIdle(maxIdle); // 连接池中最大的空闲的连接数，超过的空闲连接将被释放，如果设置为负数表示不限
        dataSource.setMinIdle(minIdle); // 连接池中最小的空闲的连接数，低于这个数量会被创建新的连接
        dataSource.setMaxWait(maxWait); // 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，超过该时间限制会抛出异常，如果设置-1表示无限等待
        dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout); // 超过时间限制，回收没有用(废弃)的连接
        dataSource.setRemoveAbandoned(removeAbandoned); // 超过removeAbandonedTimeout时间后，是否进 行没用连接（废弃）的回收
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis); // 检查无效连接的时间间隔 设为30分钟

        return dataSource;
    }
}
