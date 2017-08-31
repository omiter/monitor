package com.gome.monitor.service;

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
    public void mysqlmonitorlogs();
}
