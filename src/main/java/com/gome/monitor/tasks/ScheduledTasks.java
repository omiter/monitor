package com.gome.monitor.tasks;

import com.gome.monitor.bean.ShellBean;
import com.gome.monitor.component.MyEmailReceiver;
import com.gome.monitor.component.PropConfig;
import com.gome.monitor.component.ShellConnection;
import com.gome.monitor.service.EmailService;
import com.gome.monitor.service.MonitorCountService;
import com.gome.monitor.service.MysqlmonitorService;
import com.gome.monitor.util.DateNewUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Autowired
    MonitorCountService monitorCountService;

    @Autowired
    MyEmailReceiver receiver;

    @Autowired
    ShellConnection shellConnection;


    private boolean isSendAble(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value)) {
            stringRedisTemplate.opsForValue().set(key, "0_" + DateNewUtils.getTime());
        } else {
            String[] r = value.split("_");
            int index = Integer.valueOf(r[0]);
            long old = Long.valueOf(r[1]);
            long curr = DateNewUtils.getTime();
            Long time = 0L;
            if (index > DateNewUtils.timeList.size() -1){
                time = DateNewUtils.ONE_HOUR;
            }else {
                time = DateNewUtils.timeList.get(index);
            }
            if (curr - old < time) return false;
            stringRedisTemplate.opsForValue().set(key, 3 + "_" + curr);
        }
        return true;
    }

    @Scheduled(cron = "30 * * * * *")
    public void appMonitor() {
        try {
//            Connection conn = ShellUtils.remoteLogin(propConfig.getShellHost(), propConfig.getShellUser(), propConfig.getShellPwd());
            ShellBean bean = ShellBean.builder().command(propConfig.getShellAppScript()).build();
            bean = shellConnection.exec(bean);
            List<String> monitors = Arrays.asList(bean.getOut().split("\\n"));

            for (String monitor : monitors) {
                String[] states = monitor.split("###");
                if (states[0].equals("0")) {
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", states[1]);
                    hashMap.put("path", states[2]);
                    hashMap.put("app", states[3]);
                    String key = states[1] + ":" + states[3] + ":" + states[2];
                    if (!isSendAble(key)) continue;
                    emailService.sendModelMail(propConfig.getMailTo(), "Process monitor: " + states[3], hashMap, "monitor_email.vm");
                } else {
                    if (states.length < 2) continue;
                    List<String> list = Arrays.asList(states[1].split("~~~"));
                    for (String info : list) {
                        log.debug(info);
                        String[] split = info.split("===");
                        String[] split1 = split[3].split(",");

                        String key = split[0] + ":" + split[1] + ":" + split[2];
                        String[] status = split1[1].toUpperCase().split(":::");
                        if (!CollectionUtils.containsAny(Arrays.asList(status),
                                Arrays.asList(propConfig.getIgnoreStatus().replaceAll(" ", "").toUpperCase().split(",")))) {
                            key = key + ":" + split1[1];
                            Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("host", split[0]);
                            hashMap.put("type", split[1]);
                            hashMap.put("app", split[2]);
                            hashMap.put("status", split1[1]);
                            if (!isSendAble(key)) continue;
                            emailService.sendModelMail(propConfig.getMailTo(), "Process monitor App:" + split[2], hashMap, "process_email.vm");
                        } else {
                            Set<String> keys = stringRedisTemplate.keys(key + "*");
                            for (String k : keys) {
                                stringRedisTemplate.delete(k);
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void logErrMonitor() {
        try {
//            Connection conn = ShellUtils.remoteLogin(propConfig.getShellHost(), propConfig.getShellUser(), propConfig.getShellPwd());
            ShellBean bean = ShellBean.builder().command(propConfig.getShellLogErrScript()).build();
            bean = shellConnection.exec(bean);
            List<String> monitors = Arrays.asList(bean.getOut().split("\\n"));
            for (String monitor : monitors) {
                String[] states = monitor.split("###");
                if (states[0].equals("0")) {
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("host", states[1]);
                    hashMap.put("path", states[2]);
                    hashMap.put("app", states[3]);
                    String key = states[1] + ":" + states[3] + ":" + states[2];
                    if (!isSendAble(key)) continue;
                    emailService.sendModelMail(propConfig.getMailTo(), "Process monitor: " + states[3], hashMap, "monitor_email.vm");
                } else {
                    if (states.length < 2) continue;
                    List<String> list = Arrays.asList(states[1].split("~~~"));
                    for (String info : list) {
                        String[] split = info.split("===");
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 6 * * * ")
    public void checkmysqlstate() {
        mysqlmonitorService.mysqlmonitorstate();
    }

    @Scheduled(cron = "0 0 10-20/2 * * *")
    public void checkmaindata() {
        String maindata = stringRedisTemplate.opsForValue().get("主数据");
        //String histroysaledata = stringRedisTemplate.opsForValue().get("历史销售记录测试").toString();
        String salehydata = stringRedisTemplate.opsForValue().get("销售单会员数据");
        log.info("主数据 " + maindata + " " + "" + " 销售单会员数据" + salehydata);
        List<Map<String, Object>> mysqlmonitorlogs = mysqlmonitorService.mysqlmonitorlogs();
        for (int i = 0; i < mysqlmonitorlogs.size(); i++) {
            String name = mysqlmonitorlogs.get(i).get("name").toString();
            String amount = mysqlmonitorlogs.get(i).get("amount").toString();
            if (i == 0 && amount.equals(salehydata)) {
                emailService.sendSimpleMail(propConfig.getMailTo(), "主数据未发生改变", " 销售单会员数据未发生改变");
            } else if (i == 1 && amount.equals(maindata)) {
                emailService.sendSimpleMail(propConfig.getMailTo(), "主数据未发生改变", "主数据未发生改变");
            }
            stringRedisTemplate.opsForValue().getAndSet(name, amount);

        }
    }


    @Scheduled(cron = "0 0/30 * * * *")
    public void countMonitor() {
        log.info("check job data count...");
        monitorCountService.monitorCount();
    }

//
//    @Scheduled(cron = "0 * * * * *")
//    public void EmailReceiver() {
//        LocalDateTime now = LocalDateTime.now();
//
//        try {
//            Object[] receive = receiver.receive();
//            for (Object o : receive) {
//                MimeMessage message = (MimeMessage) o;
//                LocalDateTime dateTime = ZonedDateTime.ofInstant(message.getSentDate().toInstant(), ZoneId.systemDefault()).toLocalDateTime();
//                String subject = message.getSubject();
//                if (DateNewUtils.isContains(dateTime, now, -1, ChronoUnit.MINUTES)
//                        && subject.trim().startsWith("exec:")) {
//                    //部分命令不允许执行
//                    if (StringUtils.containsAny(subject, "reboot", "init", "shutdown", "halt", "poweroff", "passwd", "test")) {
//                        emailService.sendSimpleMail(propConfig.getMailTo(), "command could not exec!", subject);
//                        return;
//                    }
//
//                    String from = RexUtils.getContent(message.getHeader("From", "\n"), "<(.*?)>");
//                    if (!propConfig.getMailTo().contains(from.trim())){
//                        emailService.sendSimpleMail(propConfig.getMailTo(),"has no permission exec cmd!",from+" not in "+propConfig.getMailTo()+" \n"+ subject);
//                        return;
//                    }
//
//                    String[] split = subject.split(":")[1].split(",");
//                    ShellBean bean = null;
//                    log.info(from+" exec  '"+split[2]+"'  start...");
//                    if (split.length == 1) {
//                        bean = ShellBean.builder().command(split[0]).build();
//                        bean = shellConnection.exec(bean);
//                    } else {
//                        bean = ShellBean.builder().host(split[0].trim()).user(split[1].trim()).command(split[2].trim()).build();
//                        bean = shellConnection.exec(bean, true);
//                    }
//                    log.info(from+" exec end...");
//                    System.out.println(bean);
//                    emailService.sendSimpleMail(propConfig.getMailTo(), "command '" + split[2] + "' exec result:", from+" exec result:+\n "+bean.toString());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
