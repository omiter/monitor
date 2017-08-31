package com.gome.monitor.tasks;

import com.gome.monitor.service.EmailService;
import com.gome.monitor.util.ShellUtils;
import com.trilead.ssh2.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableScheduling
@Component
public class ScheduledTasks {

    @Autowired
    EmailService emailService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 * * * * *")
    public void appMonitor() {
        try {
            Connection conn = ShellUtils.remoteLogin("10.112.167.26", "root", "3edcXZAQ!");
            String[] pwd = ShellUtils.remoteExec("/data12/monitor/bin/monitor.sh /data12/monitor/bin/list.conf", conn);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));

            for (String info : list) {
                String[] split = info.split(",");
                String[] split1 = split[3].split(" ");
                if (!"RUNNING".equals(split1[1].toUpperCase())) {
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", split[0]);
                    hashMap.put("type", split[1]);
                    hashMap.put("app", split[2]);
                    hashMap.put("status", split1[1]);
                    String key = hashMap.get("host")+":"+hashMap.get("type")+":"+hashMap.get("status")+":"+hashMap.get("app");
                    String value = stringRedisTemplate.opsForValue().get(key);
                    if (!StringUtils.isEmpty(value)){
//                        DateNewUtils.get
                    }
                    emailService.sendModelMail("1322786333@qq.com", split[2] + " monitor", hashMap,"process_email.vm");
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
            Connection conn = ShellUtils.remoteLogin("10.112.167.26", "root", "3edcXZAQ!");
            String[] pwd = ShellUtils.remoteExec("/data12/monitor/bin/monitor_log.sh /data12/monitor/bin/list.conf", conn);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));

            for (String info : list) {
                String[] split = info.split(",");
                if (!"0".equals(split[3].trim())){
                    List<String> infos = Arrays.asList(split[3].split(":::"));
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", split[0]);
                    hashMap.put("type", split[1]);
                    hashMap.put("app", split[2]);
                    hashMap.put("infos", infos);
                    emailService.sendModelMail("1322786333@qq.com", split[2] + " monitor", hashMap,"err_log_email.vm");
                }
            }
            assert conn != null;
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
