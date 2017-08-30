package com.gome.monitor.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hutao on 2017/7/14.
 */
public class SqlUtils {

    public static Map<String,String> load(String sqlFile){
        Map<String,String> map = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(SqlUtils.class.getClassLoader().getResourceAsStream(sqlFile),"UTF-8"));
            String line = null;
            String sqlName=null;
            int i = 0;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine())!=null){
                i++;
                if (line.replaceAll(" ","").startsWith("--#")){
                    sqlName = line.split("#")[1].trim();
                    continue;
                }else if(line.startsWith("--")){
                    continue;
                }
                if (line.trim().endsWith(";")){
                    sb.append(" ").append(line);
                    map.put(sqlName==null?i+"":sqlName,sb.toString());
                    sb = new StringBuilder();
                    sqlName = null;
                    continue;
                }
                sb.append(" ").append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
            try {
                assert null != reader;
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return map;
    }
    public static List<String> loadSql(String sqlFile){
        Map<String, String> map = load(sqlFile);
        return map.values().stream().map(sql->sql.replace(";","")).collect(Collectors.toList());
    }


    public static String loadSql(String sqlFile,String name){
        return load(sqlFile).get(name).replace(";","");
    }



    public static String replaceSql(String sql, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                sql = sql.replaceAll("\\{" + key + "}", map.get(key));
            }
        }
        return sql;
    }

    public static String replaceSql(String sql, String o,String n) {
        return sql.replaceAll("\\{" + o + "}", n);
    }
}
