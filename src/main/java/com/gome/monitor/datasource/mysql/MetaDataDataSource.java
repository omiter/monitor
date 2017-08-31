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

    @Bean(name = "metaDataSource")
    public DataSource metaDataDataSource(){
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
