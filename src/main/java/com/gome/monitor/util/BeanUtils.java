package com.gome.monitor.util;

import org.apache.commons.beanutils.ConvertUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hutao on 2017/7/20.
 */
public class BeanUtils {


    public static Map<String, Object> mapToMap(Map<String, Object> map) {
        Map<String, Object> map1 = new HashMap<>();
        for (String key : map.keySet()) {
            map1.put(key.split("\\.")[1], map.get(key));
        }
        return map1;
    }

    public static <U> U convertMap(Class<U> type, Map<String, Object> map) {
        // 创建 JavaBean 对象
        U obj = null;

        try {
            // 获取类属性
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            obj = type.newInstance();
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName().toLowerCase(
                        Locale.getDefault());

                if (map.containsKey(propertyName)) {
                    String value = ConvertUtils.convert(map.get(propertyName)).replace("\r","");
                    Object[] args = new Object[1];
                    args[0] = ConvertUtils.convert(value, descriptor
                            .getPropertyType());

                    descriptor.getWriteMethod().invoke(obj, args);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Map<String, Object> transBean2Map(Object bean)  {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnMap;
    }
}
