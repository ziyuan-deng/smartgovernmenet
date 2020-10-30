package com.neco.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 反射工具类
 * @author ziyuan_deng
 * @date 2020/9/4
 */
public class ReflectUtil {
	
	private static final String EXP = "class";
	
    public static Object getFieldValue(Object obj , String fieldName ){
        if(obj == null){
            return null ;
        }
        Field targetField = getTargetField(obj.getClass(), fieldName);
        try {
            return FieldUtils.readField(targetField, obj, true) ;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null ;
    }

    
    public static Field getTargetField(Class<?> targetClass, String fieldName) {
        Field field = null;
        try {
            if (targetClass == null) {
                return field;
            }
            if (Object.class.equals(targetClass)) {
                return field;
            }
            field = FieldUtils.getDeclaredField(targetClass, fieldName, true);
            if (field == null) {
                field = getTargetField(targetClass.getSuperclass(), fieldName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }

    /**
     * 设置属性值
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object obj , String fieldName , Object value ){
        if(null == obj){return;}
        Field targetField = getTargetField(obj.getClass(), fieldName);
        try {
            FieldUtils.writeField(targetField, obj, value) ;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * bean转map
     * @param obj
     * @param filterBlank
     * @return
     */
    public static Map<String,Object> beanToMap(Object obj,boolean filterBlank){
        if(obj == null){
            return null;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals(EXP)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if(value != null){
                        if(filterBlank && value instanceof String){
                            if(StringUtils.isNotBlank(String.valueOf(value))){
                                map.put(key, value);
                            }
                            continue;
                        }

                        map.put(key, value);
                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return map;
    }
    
	/**
	 * @notes: 获取为空的属性名
	 * @author: wei
	 * @createTime: 2018年10月30日 下午4:33:53
	 *
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null){
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    
}
