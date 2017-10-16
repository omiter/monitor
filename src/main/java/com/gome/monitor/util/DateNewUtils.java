package com.gome.monitor.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateNewUtils {

    public static final String zone_format_patten="EEE, d MMM yyyy HH:mm:ss ZZZ (zzz)";

    public static final DateTimeFormatter yyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter yyyy_MM_ddTZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateTimeFormatter yyyyMMddHH = DateTimeFormatter.ofPattern("yyyyMMddHH");
    public static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter zone_format = DateTimeFormatter.ofPattern(zone_format_patten);

    public static final long ONE_MIN = 1000 * 60;
    public static final long FIVE_MIN = 1000 * 60 * 5;
    public static final long TEN_MIN = 1000 * 60 * 10;
    public static final long HALF_HOUR = 1000 * 60 * 30;
    public static final long ONE_HOUR = 1000 * 60 * 60;
    public static final long ONE_DAY = 1000 * 60 * 60 * 24;
    public static final long ONE_MONTH = 1000 * 60 * 60 * 24 * 30;
    public static final long ONE_YEAR = 1000 * 60 * 60 * 24 * 30 * 365;

    public static final List<Long> timeList = Arrays.asList(ONE_MIN,FIVE_MIN,TEN_MIN,HALF_HOUR,ONE_HOUR,ONE_DAY,ONE_MONTH,ONE_YEAR);

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

    public static Date toDate(ZonedDateTime dateTime){
       return Date.from(dateTime.toInstant());
    }

    public static ZonedDateTime toDateTime(Date date){
        return ZonedDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
    }

    public static boolean isContains(LocalDateTime date, LocalDateTime curr, int num, TemporalUnit unit){
        LocalDateTime last = curr.plus(num, unit);
        long t1 = Date.from(date.toInstant(ZoneOffset.UTC)).getTime();
        long t2 = Date.from(curr.toInstant(ZoneOffset.UTC)).getTime();
        long t3 = Date.from(last.toInstant(ZoneOffset.UTC)).getTime();
        return ((t1-t2) * 1.0 / (t1-t3)) < 0;
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
    public static LocalDateTime getLocalDateTime(String patten,String date) {
        return LocalDateTime.parse(date,DateTimeFormatter.ofPattern(patten));
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

    public static String getDateTimeZone(String date,String patten,Locale locale){
        ZonedDateTime dateTime = ZonedDateTime.parse(date, DateTimeFormatter.ofPattern(patten, locale));
        return dateTime.toLocalDateTime().format(yyyy_MM_dd_HH_mm_ss);
    }

    public static void main(String[] args) {

        String s ="From: =?gb2312?B?aHV0YW8ouvrMzi6088r9vt3R0L6/1Louyv2+3ca9zKiyvyk=?= <hutao@gomeplus.com><1183008058@qq.com>";
        Pattern p = Pattern.compile("<(.*?)>");
        Matcher m = p.matcher(s);
        while (m.find()){
            String group = m.group(1);
            System.out.println(group);
        }
    }
}
