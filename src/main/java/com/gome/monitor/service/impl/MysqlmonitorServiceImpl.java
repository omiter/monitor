package com.gome.monitor.service.impl;

import com.gome.monitor.component.PropConfig;
import com.gome.monitor.dao.MysqldataQueryDao;
import com.gome.monitor.service.MysqlmonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        String sql="select job_id,a.project_name as project_name,module_name,job_name,batch_begin_time,last_exec_time,job_status , mail_to,mail_template,mail_subject,mail_comment from jobs a LEFT JOIN jobs_mail t on " +
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
                String contant="第"+job_id+"任务报错，项目名称："+project_name+"   模块名称："+module_name+"   任务名称："+job_name
                        +"   batch_begin_time："+batch_begin_time+"   最新执行时间："+last_exec_time;
               if(meta.get("mail_to")!=null){
                   String mail_subject = meta.get("mail_subject").toString();
                   String mail_template = meta.get("mail_template").toString();
                   String mail_comment = meta.get("mail_comment").toString();
                   Map<String, Object> map = new HashMap<>();
                   map.put("mail_subject",mail_subject);
                   map.put("ID",job_id);
                   map.put("project_name",project_name);
                   map.put("module_name",module_name);
                   map.put("job_name",job_name);
                   map.put("batch_begin_time",batch_begin_time);
                   map.put("last_exec_time",last_exec_time);
                   map.put("mail_comment",mail_comment);
                   emailServiceimpl.sendModelMail(meta.get("mail_to").toString(),mail_subject,map,mail_template);
                   //emailServiceimpl.sendSimpleMail(meta.get("mail_to").toString(),"任务流程表报错",contant);
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
