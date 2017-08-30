package com.gome.monitor.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hutao on 2017/8/2.
 */
public class CollectionUtils {

    public static <T> List<List<T>> splitListByPage(List<T> list,int num){
        int size = list.size();
        int count = (int)(size * 1.0 / num);
        List<List<T>> li = new ArrayList<>();
        for (int i = 0;i<num;i++){
            if (i == num -1){
                li.add(list.subList(i*count,size));
                break;
            }
            li.add(list.subList(i*count,(i+1)*count));
        }
        return li;
    }

    public static <T> List<List<T>> splitListByCount(List<T> list,int count){
        int size = list.size();
        int num = (size -1)/count +1;
        List<List<T>> li = new ArrayList<>();
        for (int i = 0;i<num;i++){
            if (i == num -1){
                li.add(list.subList(i*count,size));
                break;
            }
            li.add(list.subList(i*count,(i+1)*count));
        }
        return li;
    }

}
