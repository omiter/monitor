package com.gome.monitor.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateNewUtils {

    public static final DateTimeFormatter yyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter yyyy_MM_ddTZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateTimeFormatter yyyyMMddHH = DateTimeFormatter.ofPattern("yyyyMMddHH");
    public static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final long ONE_MIN = 1000 * 60;
    public static final long ONE_HOUR = 1000 * 60 * 60;
    public static final long ONE_DAY = 1000 * 60 * 60 * 24;
    public static final long ONE_MONTH = 1000 * 60 * 60 * 24 * 30;
    public static final long ONE_YEAR = 1000 * 60 * 60 * 24 * 30 * 365;

    public static final List<Long> timeList = Arrays.asList(ONE_MIN,ONE_HOUR,ONE_DAY,ONE_MONTH,ONE_YEAR);

    public static String getToday() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getMonthFirstDay(String date) {
        LocalDate today = LocalDate.parse(date);
        int year = today.getYear();
        Month month = today.getMonth();
        LocalDate monthFirstDay = LocalDate.of(year, month, 1);
        return monthFirstDay.format(yyyy_MM_dd);
    }

    public static List<String> getListDays(String startDay, String endDay) {
        LocalDate start = LocalDate.parse(startDay);
        LocalDate end = LocalDate.parse(endDay);
        ArrayList<String> days = new ArrayList<>();
        while (!start.equals(end)) {
            days.add(start.format(yyyy_MM_dd));
            start = start.plusDays(1);
        }
        return days;
    }

    public static List<String> getMonthFirstDayToToday() {
        String localDate = getToday();
        return getListDays(getMonthFirstDay(localDate), localDate);
    }


    public static String getDateToDay(String date, int to) {
        return LocalDate.parse(date).plusDays(to).format(yyyy_MM_dd);
    }

    public static String getDateTime(String patten) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patten));
    }


    public static String getTodayToCurrentInterval() {
        String start = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00.000'Z'"));
        return start + "/" + end;
    }

    public static String reFormatDate(String d, DateTimeFormatter origin, DateTimeFormatter target) {
        return LocalDateTime.parse(d, origin).format(target);
    }

    public static long getTime(){
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

//    public static void main(String[] args) {
//        System.out.println(getTime());
//    }
}
