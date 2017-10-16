package com.gome.monitor.service.impl;

import com.gome.monitor.bean.Job;
import com.gome.monitor.bean.JobDataCount;
import com.gome.monitor.component.PropConfig;
import com.gome.monitor.dao.MonitorCountDao;
import com.gome.monitor.service.EmailService;
import com.gome.monitor.service.MonitorCountService;
import com.gome.monitor.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonitorCountServiceImpl implements MonitorCountService {

    @Autowired
    MonitorCountDao monitorCountDao;
    @Autowired
    EmailService emailService;
    @Autowired
    PropConfig propConfig;

    public void monitorCount(){
        List<Map<String, Object>> maps1 = monitorCountDao.queryCount();
        List<Map<String, Object>> maps = monitorCountDao.queryJob();
        Map<String, Job> jobMap = maps.stream().map(m -> BeanUtils.convertMap(Job.class, m)).collect(Collectors.toMap(Job::getId, job -> job));
        List<JobDataCount> jobDataCounts = maps1.stream().map(m -> BeanUtils.convertMap(JobDataCount.class, m)).collect(Collectors.toList());

        for (JobDataCount jobDataCount:jobDataCounts){
            if (jobMap.containsKey(jobDataCount.getJob_id())){
                Job job = jobMap.get(jobDataCount.getJob_id());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("project",job.getProject());
                hashMap.put("job",job.getJob());
                hashMap.put("host",job.getHost());
                hashMap.put("path",job.getPath());
                hashMap.put("time",jobDataCount.getExec_time());
                hashMap.put("count",jobDataCount.getCount());
                hashMap.put("job_date",jobDataCount.getJob_date());
                emailService.sendModelMail(propConfig.getMailTo(),"count monitor project:"+job.getProject(),hashMap,"count_email.vm");
                monitorCountDao.update(jobDataCount.getId(),"1");
            }
        }
    }
}
