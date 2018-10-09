package com.gome.monitor;

import com.gome.monitor.bean.ShellBean;
import com.gome.monitor.component.MyEmailReceiver;
import com.gome.monitor.component.PropConfig;
import com.gome.monitor.component.ShellConnection;
import com.gome.monitor.service.EmailService;
import com.gome.monitor.service.MonitorCountService;
import com.gome.monitor.service.MysqlmonitorService;
import com.gome.monitor.tasks.ScheduledTasks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorApplicationTests {

    @Autowired
    ScheduledTasks scheduledTasks;
    @Autowired
    MysqlmonitorService mysqlmonitorService;

    @Autowired
    EmailService emailService;

    @Autowired
    MonitorCountService monitorCountService;

    @Autowired
    MyEmailReceiver receiver;

    @Autowired
    ShellConnection shellConnection;

    @Autowired
    PropConfig propConfig;


    @Test
    public void contextLoads() {
//        monitorCountService.monitorCount();
    }

    @Test
    public void test() {
//        mysqlmonitorService.mysqlmonitorstate();
    }

    @Test
    public void test2() {
//        List<Map<String, Object>> mysqlmonitorlogs = mysqlmonitorService.mysqlmonitorlogs();
//        System.out.print(mysqlmonitorlogs);
    }

    @Test
    public void test3(){
//        emailService.sendSimpleMail("hutao@gomeplus.com","test","111111111111111");
    }


    @Test
    public void test4() throws MessagingException, IOException, InterruptedException {
        ShellBean ll = ShellBean.builder().command(propConfig.getShellAppScript()).build();
        ShellBean exec = shellConnection.exec(ll);
        System.out.println(exec.getOut());
    }
}
