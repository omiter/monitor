package com.gome.monitor.util;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CronUtils {

    public static LocalDateTime getNextExecTime(String cron){
        CronSequenceGenerator generator = new CronSequenceGenerator(cron);
        Date next = generator.next(new Date());
        return next.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    public static LocalDateTime getNextExecTime(String cron,LocalDateTime date){
        CronSequenceGenerator generator = new CronSequenceGenerator(cron);
        Date next = generator.next(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
        return next.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static List<LocalDateTime> getNextExecTimes(String cron,int times){
        LocalDateTime dateTime = LocalDateTime.now();
        ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
        for (int i = 0;i<times;i++){
            dateTime = getNextExecTime(cron,dateTime);
            dateTimes.add(dateTime);
        }
        return dateTimes;
    }

    public static List<String> getNextExecTimesStr(String cron,int times){
        return getNextExecTimes(cron,times).stream().map(m -> m.format(DateNewUtils.yyyy_MM_dd_HH_mm_ss)).collect(Collectors.toList());
    }

    public static String getNextExecTimeStr(String cron){
        LocalDateTime nextExecTime = getNextExecTime(cron);
        return nextExecTime.format(DateNewUtils.yyyy_MM_dd_HH_mm_ss);
    }

//    public static void main(String[] args) {
//        System.out.println(getNextExecTimesStr("00 10 1 * * *",5));
//    }
}
