package com.gome.monitor.tasks;

import com.gome.monitor.service.EmailService;
import com.gome.monitor.util.ShellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@EnableScheduling
@Component
public class ScheduledTasks {

    @Autowired
    EmailService emailService;

    @Scheduled(cron = "*/10 * * * * *")
    public void test(){
        try {
            boolean b = ShellUtils.remoteLogin("10.112.167.26", "root", "3edcXZAQ!");
            System.out.println(b);
            String[] pwd = ShellUtils.remoteExec("/data12/monitor/bin/monitor.sh /data12/monitor/bin/list.conf");
            System.out.println(pwd[0]);
            List<String> list = Arrays.asList(pwd[1].split("\\n"));

            for (String info:list){
                String[] split = info.split(",");
                String[] split1 = split[3].split(" ");
                if (!"RUNNING".equals(split1[1].toUpperCase())){
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host",split[0]);
                    hashMap.put("type",split[1]);
                    hashMap.put("app",split[2]);
                    hashMap.put("status",split1[1]);
                    System.out.println(hashMap);
                    emailService.sendModelMail("1322786333@qq.com",split[2]+" monitor",hashMap);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
