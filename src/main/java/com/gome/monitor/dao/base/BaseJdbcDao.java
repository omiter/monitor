package com.gome.monitor.dao.base;


import com.gome.monitor.datasource.mysql.MetaDataDataSourceDaoSupport;
import com.gome.monitor.datasource.mysql.MetadataflowSourceDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by hutao on 2017/7/19.
 */
@Component("baseJdbcDao")
public class BaseJdbcDao extends NamedParameterJdbcDaoSupport {


    @Autowired
    MetadataflowSourceDaoSupport metadataflowSourceDaoSupport;
    @Autowired
    MetaDataDataSourceDaoSupport metaDataDataSourceDaoSupport;
    @PostConstruct
    public void init() {
        this.setJdbcTemplate(metadataflowSourceDaoSupport.jdbcTemplate());
    }

    public JdbcTemplate getMetaDataTemplate() {
        return metaDataDataSourceDaoSupport.getMetaDataJdbcTemplate();
    }


}
