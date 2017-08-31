package com.gome.monitor.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by lujiali on 2017/8/31.
 */
public interface MysqldataQueryDao {
    public List<Map<String, Object>> Querydata(String sql);
    public List<Map<String, Object>> Querymaindata(String sql);

}
