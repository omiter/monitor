package com.gome.monitor.dao.impl;

import com.gome.monitor.dao.MysqldataQueryDao;
import com.gome.monitor.dao.base.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by lujiali on 2017/8/31.
 */
@Repository
public class MysqldataQueryDaoImpl extends BaseJdbcDao implements MysqldataQueryDao{
    @Override
    public  List<Map<String, Object>> Querydata(String sql) {
        List<Map<String, Object>> maps = getJdbcTemplate().queryForList(sql);
        return  maps;
    }

    @Override
    public List<Map<String, Object>> Querymaindata(String sql) {
        List<Map<String, Object>> maps = getMetaDataTemplate().queryForList(sql);
        return maps;
    }
}
