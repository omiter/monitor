package com.gome.monitor.tasks;

import com.gome.monitor.component.PropConfig;
import com.gome.monitor.service.EmailService;
import com.gome.monitor.service.MysqlmonitorService;
import com.gome.monitor.util.DateNewUtils;
import com.gome.monitor.util.ShellUtils;
import com.trilead.ssh2.Connection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@EnableScheduling
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    EmailService emailService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PropConfig propConfig;

    @Autowired
    MysqlmonitorService mysqlmonitorService;

    @Scheduled(cron = "0 * * * * *")
    public void appMonitor() {
        try {
            Connection conn = ShellUtils.remoteLogin(propConfig.getShellHost(), propConfig.getShellUser(), propConfig.getShellPwd());
            String[] pwd = ShellUtils.remoteExec(propConfig.getShellAppScript(), conn);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));
            List<String> ignoreList = Arrays.asList(propConfig.getIgnoreStatus().toUpperCase().replaceAll(" ", "").split(","));

            for (String info : list) {
                log.debug(info);
                String[] split = info.split(",");
                String[] split1 = split[3].split(" ");

                String key = split[0] + ":" + split[1] + ":" + split[2];
                String[] status = split1[1].toUpperCase().split(":::");
                if (!CollectionUtils.containsAny(Arrays.asList(status),
                        Arrays.asList(propConfig.getIgnoreStatus().replaceAll(" ","").toUpperCase().split(",")))) {
                    key = key + ":" + split1[1];
                    String value = stringRedisTemplate.opsForValue().get(key);
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", split[0]);
                    hashMap.put("type", split[1]);
                    hashMap.put("app", split[2]);
                    hashMap.put("status", split1[1]);
                    if (StringUtils.isEmpty(value)) {
                        stringRedisTemplate.opsForValue().set(key, "0_" + DateNewUtils.getTime());
                    } else {
                        String[] r = value.split("_");
                        int index = Integer.valueOf(r[0]);
                        long old = Long.valueOf(r[1]);
                        long curr = DateNewUtils.getTime();
                        if (curr - old < DateNewUtils.timeList.get(index)) continue;
                        stringRedisTemplate.opsForValue().set(key, (index + 1) + "_" + curr);
                    }
                    emailService.sendModelMail(propConfig.getMailTo(), "Process monitor App:" + split[2], hashMap, "process_email.vm");
                } else {
                    Set<String> keys = stringRedisTemplate.keys(key + "*");
                    for (String k : keys) {
                        stringRedisTemplate.delete(k);
                    }
                }
            }
            assert conn != null;
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void logErrMonitor() {
        try {
            Connection conn = ShellUtils.remoteLogin(propConfig.getShellHost(), propConfig.getShellUser(), propConfig.getShellPwd());
            String[] pwd = ShellUtils.remoteExec(propConfig.getShellLogErrScript(), conn);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));

            for (String info : list) {
                String[] split = info.split(",");
                if (!"0".equals(split[3].trim())) {
                    List<String> infos = Arrays.asList(split[3].split(":::"));
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", split[0]);
                    hashMap.put("type", split[1]);
                    hashMap.put("app", split[2]);
                    hashMap.put("infos", infos);
                    emailService.sendModelMail(propConfig.getMailTo(), "Log monitor App:" + split[2], hashMap, "err_log_email.vm");
                }
            }
            assert conn != null;
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 8 * * * ")
    public void checkmysqlstate(){
        mysqlmonitorService.mysqlmonitorstate();
    }
    @Scheduled(cron = "0 0 10-20/2 * * *")
    public void checkmaindata(){
        String maindata = stringRedisTemplate.opsForValue().get("主数据").toString();
        String histroysaledata = stringRedisTemplate.opsForValue().get("历史销售记录测试").toString();
        String salehydata = stringRedisTemplate.opsForValue().get("销售单会员数据").toString();
        log.info("主数据 "+maindata+" 历史销售记录测试"+histroysaledata+" 销售单会员数据"+salehydata);
        List<Map<String, Object>> mysqlmonitorlogs = mysqlmonitorService.mysqlmonitorlogs();
        for(int i=0;i<mysqlmonitorlogs.size();i++){
            String name = mysqlmonitorlogs.get(i).get("name").toString();
            String amount = mysqlmonitorlogs.get(i).get("amount").toString();
            if(i==0 && amount.equals(salehydata)){
                emailService.sendSimpleMail(propConfig.getMailTo(),"主数据未发生改变"," 销售单会员数据未发生改变");
            }else if(i==1 && amount.equals(maindata)){
                emailService.sendSimpleMail(propConfig.getMailTo(),"主数据未发生改变","主数据未发生改变");
            }else if(i==2 && amount.equals(histroysaledata)){
                emailService.sendSimpleMail(propConfig.getMailTo(),"主数据未发生改变","历史销售记录测试未发生改变");
            }
            stringRedisTemplate.opsForValue().getAndSet(name, amount);

        }
    }
}
