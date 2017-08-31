package com.gome.monitor.datasource.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by hutao on 2017/7/19.
 */
@Component
public class MetaDataDataSourceDaoSupport {


    @Autowired
    @Qualifier("metaDataSource")
    DataSource dataSource;

    public JdbcTemplate getMetaDataJdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }
}
