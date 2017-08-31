package com.gome.monitor.datasource.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by hutao on 2017/7/19.
 */
@Component
public class MetadataflowSourceDaoSupport {

    @Autowired
    @Qualifier("datasource")
    DataSource dataSource;

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }
}
