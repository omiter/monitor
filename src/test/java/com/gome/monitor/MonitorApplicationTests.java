package com.gome.monitor;

import com.gome.monitor.service.EmailService;
import com.gome.monitor.service.MysqlmonitorService;
import com.gome.monitor.tasks.ScheduledTasks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorApplicationTests {

    @Autowired
    ScheduledTasks scheduledTasks;
    @Autowired
    MysqlmonitorService mysqlmonitorService;

    @Autowired
    EmailService emailService;

    @Test
    public void contextLoads() {
		scheduledTasks.logErrMonitor();
    }

    @Test
    public void test() {
        mysqlmonitorService.mysqlmonitorstate();
    }

    @Test
    public void test2() {
        List<Map<String, Object>> mysqlmonitorlogs = mysqlmonitorService.mysqlmonitorlogs();
        System.out.print(mysqlmonitorlogs);
    }

    @Test
    public void test3(){
        emailService.sendSimpleMail("hutao@gomeplus.com","test","111111111111111");
    }
}
