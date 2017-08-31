package com.gome.monitor.tasks;

import com.gome.monitor.component.PropConfig;
import com.gome.monitor.service.EmailService;
import com.gome.monitor.util.DateNewUtils;
import com.gome.monitor.util.ShellUtils;
import com.trilead.ssh2.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@EnableScheduling
@Component
public class ScheduledTasks {

    @Autowired
    EmailService emailService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PropConfig propConfig;

    @Scheduled(cron = "0 * * * * *")
    public void appMonitor() {
        try {
            Connection conn = ShellUtils.remoteLogin(propConfig.getShellHost(), propConfig.getShellUser(), propConfig.getShellPwd());
            String[] pwd = ShellUtils.remoteExec(propConfig.getShellAppScript(), conn);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));
            List<String> ignoreList = Arrays.asList(propConfig.getIgnoreStatus().toUpperCase().replaceAll(" ", "").split(","));

            for (String info : list) {
                String[] split = info.split(",");
                String[] split1 = split[3].split(" ");

                String key = split[0] + ":" + split[1] + ":" + split[2];
                if (!ignoreList.contains(split1[1].trim().toUpperCase())) {
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
        }
    }
}
