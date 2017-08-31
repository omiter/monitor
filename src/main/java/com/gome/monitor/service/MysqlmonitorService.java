package com.gome.monitor.service;

import java.util.List;
import java.util.Map;

/**
 * Created by lujiali on 2017/8/31.
 */
public interface MysqlmonitorService {

    /**
     * 监控工作流
     */
    public void mysqlmonitorstate();

    /**
     * 监控主数据
     */
    public List<Map<String, Object>> mysqlmonitorlogs();
}
