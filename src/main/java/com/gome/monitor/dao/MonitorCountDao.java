package com.gome.monitor.dao;

import java.util.List;
import java.util.Map;

public interface MonitorCountDao {

    public List<Map<String, Object>> queryCount();
    public List<Map<String, Object>> queryJob();
    public void update(String id,String state);
}
