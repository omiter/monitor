package com.gome.monitor.service.impl;

import com.gome.monitor.component.PropConfig;
import com.gome.monitor.dao.MysqldataQueryDao;
import com.gome.monitor.service.MysqlmonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lujiali on 2017/8/31.
 */
@Service
public class MysqlmonitorServiceImpl implements MysqlmonitorService{

    @Autowired
    MysqldataQueryDao mysqldataQueryDao;
    @Autowired
    EmailServiceImpl emailServiceimpl;

    @Autowired
    PropConfig propConfig;

    @Override
    public void mysqlmonitorstate() {
        String sql="select job_id,a.project_name as project_name,module_name,job_name,batch_begin_time,last_exec_time,job_status , mail_to from jobs a LEFT JOIN jobs_mail t on " +
                "a.project_name=t.project_name";
        List<Map<String, Object>> querydata = mysqldataQueryDao.Querydata(sql);
        for(Map<String, Object> meta:querydata){
            String job_status = meta.get("job_status").toString();
            if(job_status.equals("1")){
                String job_id = meta.get("job_id").toString();
                String project_name = meta.get("project_name").toString();
                String module_name = meta.get("module_name").toString();
                String job_name = meta.get("job_name").toString();
                String batch_begin_time = meta.get("batch_begin_time").toString();
                String last_exec_time = meta.get("last_exec_time").toString();
                String contant="第"+job_id+"任务报错，project_name："+project_name+"   module_name："+module_name+"   job_name："+job_name
                        +"   batch_begin_time："+batch_begin_time+"   last_exec_time："+last_exec_time;
               if(meta.get("mail_to")!=null){
                   emailServiceimpl.sendSimpleMail(meta.get("mail_to").toString(),"任务流程表报错",contant);
               }else {
                   emailServiceimpl.sendSimpleMail(propConfig.getMailTo(),"任务流程表报错",contant);
               }
            }
        }
    }

    @Override
    public List<Map<String, Object>> mysqlmonitorlogs() {
        String sql="SELECT * FROM conf_domains";

        List<Map<String, Object>> querymaindata = mysqldataQueryDao.Querymaindata(sql);
        return querymaindata;
    }
}
