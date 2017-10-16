package com.gome.monitor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RexUtils {

    public static List<String> getContents(String s,String pattern){
        ArrayList<String> li = new ArrayList<>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        while (m.find()){
            String group = m.group(1);
            li.add(group);
        }
        return li;
    }

    public static String getContent(String s,String pattern){
        return getContents(s,pattern).get(0);
    }
}
