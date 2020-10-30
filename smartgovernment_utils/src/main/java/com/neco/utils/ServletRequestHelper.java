package com.neco.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求工具类
 *
 * @author 冉登寺
 * @date 2019/4/23 11:10
 */
public class ServletRequestHelper {
    /**
     * 获取请求对象
     * @return
     */
    public static HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Assert.notNull(request, "获取请求对象失败");
        return request;
    }

    /**
     * 获取请求头
     * @param name 请求头名称
     * @return
     */
    public static  String getRequestHeader(String name){
        return getRequest().getHeader(name);
    }

    /**
     * 设置请求对象属性
     * @param name 属性名称
     * @param value 属性值
     */
    public static  void setRequestAttribute(String name, Object value){
        getRequest().setAttribute(name, value);
    }

    /**
     * 获取请求属性
     * @param name 属性名称
     * @return
     */
    public static  <T> T getRequestAttribute(String name){
        return (T) getRequest().getAttribute(name);
    }

    /**
     * 获取 Session 对象
     * @return
     */
    public static HttpSession getSession(){
        return getRequest().getSession();
    }


    /**
     * 设置 Session 属性值
     * @param name 属性名称
     * @param value 属性值
     */
    public static  void setSessionAttribute(String name, String value){
        getSession().setAttribute(name, value);
    }

    /**
     * 获取 Session 属性值
     * @param name 属性名称
     * @param <T>
     * @return
     */
    public static  <T> T getSessionAttribute(String name){
        return (T) getSession().getAttribute(name);
    }

    /**
     * 获取请求参数
     * @param name
     * @return
     */
    public static  String getRequestParameter(String name){
        return getRequest().getParameter(name);
    }

    /**
     * 获取查询参数
     * @return
     */
    public static  String getQueryString(){
        return getRequest().getQueryString();
    }

    /**
     * 获取URL请求参数列表
     * @return 请求参数表表，列表的每个元素为一个 Map 对象
     */
    public static  List<Map<String,String>> getQueryStringList(){
        String queryString = getQueryString();
        if(StringUtils.isEmpty(queryString))
            return null;
        String[] a = queryString.split("&");
        if(a == null || a.length == 0)
            return null;
        List<Map<String,String>> items = new ArrayList<Map<String,String>>();
        for(String s : a){
            String[] b = s.split("=");
            if(b == null || b.length <2 || StringUtils.isEmpty(b[0]))
                continue;
            Map<String,String> map = new HashMap<String,String>();
            map.put(b[0], b[1]);
            items.add(map);
        }
        return items;
    }

    /**
     * 获取URL指定名称请求参数值列表
     * @param name 请求参数名称
     * @return 值列表
     */
    public static  List<String> getQueryStringValueList(String name){
        if(StringUtils.isEmpty(name))
            return null;
        List<Map<String,String>> items = getQueryStringList();
        if(items == null || items.size() == 0)
            return null;
        List<String> values = new ArrayList<String>();
        for(Map<String,String> m : items){
            for(String k : m.keySet()){
                if(name.equals(k)){
                    values.add(m.get(k));
                    break;
                }
            }
        }
        return values;
    }

    /**
     * 获取URL请求参数指定值
     * @param name 参数名称
     * @return 参数值
     */
    public static  String getQueryStringValue(String name){
        if(StringUtils.isEmpty(name))
            return null;
        List<String> items = getQueryStringValueList(name);
        return (items == null || items.size() == 0) ? null : items.get(0);
    }

    /**
     * 获取 Cookie 数组
     * @return Cookies 数组
     */
    public static  Cookie[] getCookies(){
        return getRequest().getCookies();
    }
    /**
     * 获取指定名称的 Cookie
     * @param name Cookie 名称
     * @return Cookie 对象
     */
    public static Cookie getCookie(String name){
        if(StringUtils.isEmpty(name))
            return null;
        Cookie[] cookies = getCookies();
        if(cookies == null || cookies.length == 0)
            return null;
        for(Cookie c : cookies){
            if(name.equals(c.getName()))
                return c;
        }
        return null;
    }

    /**
     * 获取指定 Cookie 值
     * @param name Cookie 名称
     * @return Cookie 值
     */
    public static  String getCookieValue(String name){
        Cookie ck = getCookie(name);
        return ck == null ? null : ck.getValue();
    }

    /**
     * 获取请求来源页
     * @return 返回请求的来原页
     */
    public static  String getRequestReferer(){
        return getRequestHeader("referer");
    }
    /**
     * 获取IP地址
     * @return
     */
    public static String getIPAddress() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}