package com.gome.monitor.dao.impl;

import com.gome.monitor.dao.MonitorCountDao;
import com.gome.monitor.dao.base.BaseJdbcDao;
import com.gome.monitor.util.DateNewUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MonitorCountDaoImpl extends BaseJdbcDao implements MonitorCountDao {

    public List<Map<String, Object>> queryCount(){
        String sql = "SELECT * FROM monitors.job_data_count WHERE state = 0 AND count = 0 and exec_time > '"+ DateNewUtils.getToday()+"'";
        List<Map<String, Object>> query = getMetaDataTemplate().query(sql, new ColumnMapRowMapper());
        return query;
    }

    public List<Map<String, Object>> queryJob(){
        String sql = "SELECT * FROM monitors.job WHERE state = 1";
        List<Map<String, Object>> query = getMetaDataTemplate().query(sql, new ColumnMapRowMapper());
        return query;
    }

    public void update(String id,String state){
        String sql = "update monitors.job_data_count set state = "+state+" where id = "+id;
        getMetaDataTemplate().execute(sql);
    }
}
