package com.neco.utils;

import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziyuan_deng
 * @create 2020-09-06 21:15
 */
public class ComUtil {

    private static final int KB = 1024;
    private static final int MB = 1024*1024;
    private static final String iclass = "class";

    private static final String[] chars = { "0", "1", "2", "3", "4", "5", "6","7",
            "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J","K", "L","M", "N","O",
            "P", "Q", "R", "S", "T", "U","V", "W", "X", "Y", "Z", "a", "b", "c", "d",
            "e", "f", "g", "h", "i", "j", "k", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z" };

    /**
     * 格式化字节单位
     * @param bytes
     * @return
     */
    public static String formatByte(long bytes){
        DecimalFormat df = new DecimalFormat("0.0");
        StringBuffer sbuf = new StringBuffer();
        if(bytes>=100*KB){
            sbuf.append(df.format(bytes*1.0f/MB)).append("M");
        }else{
            sbuf.append(df.format(bytes*1.0f/KB)).append("K");
        }
        return sbuf.toString();
    }



    public static Date addDay(Date date, int day){
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DAY_OF_YEAR, day);
        return cl.getTime();
    }

    /**
     * 日期是否大于今天(含今天)
     * @param date
     * @return
     */
    public static boolean afterToday(Date date){
        boolean after = false;
        Calendar curDate = Calendar.getInstance();
        curDate.add(Calendar.DAY_OF_YEAR, -1);
        if(curDate.getTime().before(date)){
            after = true;
        }
        return after;
    }

    /**
     * 返回制定格式日期
     * @param format
     * @return
     */
    public static String getDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * 返回指定格式日期
     * @param format
     * @param date
     * @return
     */
    public static String getDate(String format, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }






    /***
     * 根据实体字段名生成对应的 数据库字段（大写）
     * @param field
     * @return newCol
     */
    public static String getColumn(String field){
        for(int i = 0; i < field.length(); i++){
            char cos = field.charAt(i);
            if(Character.isUpperCase(cos)){ //存在大写
                int len = field.indexOf(cos);
                field = (field.substring(0, len) + "_" + cos).toLowerCase() + field.substring(len + 1, field.length());
                String newCol1 = getColumn(field);
                if(newCol1 != null){
                    return newCol1.toUpperCase();
                }
                break;
            }
        }
        return field.toUpperCase();
    }

    /**
     * 对比两个long时间值的差距
     * @param start
     * @param end
     * @return 返回x天x时x分x秒的格式
     */
    public static String diffTime(long start, long end){
        String str = null;
        long diff = end - start;
        if(diff<=0){
            return "0天0时0分";
        }else{
            long day = 0;
            long hou = 0;
            long min = 0;
            //long ses = 0;
            day = diff/(1000*60*60*24);
            diff = diff - day*1000*60*60*24;
            hou = (diff/(1000*60*60));
            diff = diff - hou*1000*60*60;
            min = (diff/(1000*60));
            diff = diff - min*1000*60;
            //ses = (diff/1000);
            //str = String.format("%d天%d时%d分%d秒",day,hou,min,ses);
            str = String.format("%d天%d时%d分",day,hou,min);
        }

        return str;
    }

    /**
     * 返回两个日期相差的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int getDaysBetween(Date date1, Date date2) {
        Calendar cl1 = Calendar.getInstance();
        Calendar cl2 = Calendar.getInstance();
        cl1.setTime(date1);
        cl2.setTime(date2);
        if (cl1.after(cl2)) {
            Calendar swap = cl1;
            cl1 = cl2;
            cl2 = swap;
        }
        int days = cl2.get(Calendar.DAY_OF_YEAR) - cl1.get(Calendar.DAY_OF_YEAR);
        int y2 = cl2.get(Calendar.YEAR);
        if (cl1.get(Calendar.YEAR) != y2) {
            cl1 = (Calendar) cl1.clone();
            do {
                days += cl1.getActualMaximum(Calendar.DAY_OF_YEAR);
                cl1.add(Calendar.YEAR, 1);
            } while (cl1.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    public static int getInt(String str){
        int i = 0;
        try{
            i = Integer.parseInt(str);
        }catch(NumberFormatException ex){}
        return i;
    }

    public static double getDouble(String str){

        double d = 0;
        if(str!=null){
            try{
                d = Double.parseDouble(str);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return d;
    }

    /**
     * 字符串转BigDecimal
     * @param str
     * @param scale
     * @return
     */
    public static BigDecimal getBigDecimal(String str, int scale){
        double d = getDouble(str);
        BigDecimal bd = null;
        try {
            bd = BigDecimal.valueOf(d);
        } catch (Exception e) {
            bd = BigDecimal.ZERO;
        }
        bd.setScale(scale);
        return bd;
    }
    /**
     * 将多数字字符串转成BigDecimal集合
     * @param strs 包含","的数字串
     * @return
     */
    public static List<BigDecimal> getDecimalListFromStrs(String strs) {
        List<BigDecimal> decimalList = new ArrayList<>();
        if (strs==null || "".equals(strs)) {
            return decimalList;
        }
        String[] strArr = strs.split(",");
        for (String str: strArr) {
            BigDecimal bdVal = ComUtil.getBigDecimal(str, 2);
            decimalList.add(bdVal);
        }
        return decimalList;
    }


    public static String getRex(String str, String regEx) {
        String tmp = null;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()) {
            tmp = m.group();
        }
        return tmp;
    }

    public static List<String> getRexs(String str, String regEx) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    public static boolean isRex(String str, String regEx) {
        boolean b = false;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()) {
            b = true;
        }
        return b;
    }



    public static List<String> getCompareLst(List<String> arg1,List<String> arg2){
        List<String> reult = new ArrayList<String>();
        for(String p0 : arg1){
            for(String p1 : arg2){
                if(p0.equals(p1)){
                    reult.add(p1);
                }
            }
        }
        return reult;
    }




    /**
     * MD5值
     * @param s
     * @return
     */
    public static String getMD5(String s) {
        StringBuffer sbuf = new StringBuffer();
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(s.getBytes());
            byte[] buf = mdTemp.digest();
            for (byte b : buf) {
                sbuf.append(Integer.toHexString(b >> 4 & 0xF).toUpperCase());
                sbuf.append(Integer.toHexString(b & 0xF).toUpperCase());
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return sbuf.toString();
    }



    public static String randomChar(int size){
        StringBuffer sb = null;
        if(size>0){
            sb = new StringBuffer();
            Random ran = new Random();
            for(int i = 1; i <= size; i++ ){
                int r = ran.nextInt(chars.length);
                sb.append(chars[r]);
            }
        }
        return sb.toString();
    }


    /**
     * 产生一个36个字符的UUID
     * @return UUID
     */
    public static synchronized String BusinessID() {
        return randomChar(26);
    }


    /**
     * 产生一个32个字符的UUID
     * @return UUID
     */
    public static synchronized String randomUUID() {
        String s =  UUID.randomUUID().toString();
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
    }


    public static String getPercentage(Double number1, Double number2, int maximumFractionDigits){
        if(number1 != null && number2 != null && !number1.equals(0) && !number2.equals(0)){
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(maximumFractionDigits);
            String result = numberFormat.format(number1 * 100/ number2);
            return result;
        }else{
            return "0";
        }
    }

    public static String getPercentage(Integer number1, Integer number2, int maximumFractionDigits){
        if(number1 != null && number2 != null && !number1.equals(0) && !number2.equals(0)){
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(maximumFractionDigits);
            Double result = Double.valueOf(number1) / Double.valueOf(number2) * 100;
            return numberFormat.format( result);
        }else{
            return "0";
        }
    }

    /**
     * @params [key, label]
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @description:表头信息
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
    public static Map<String,String> getLabelData(String key, String label, String type, String dateType){
        Map<String,String> reMap = new HashMap<String,String>();
        reMap.put("key",key);
        reMap.put("label",label);
        reMap.put("type",type);
        reMap.put("dateType",dateType);
        return reMap;
    }

    /**
     * @params [label, key, type, data]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @description:筛选条件
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
//	public static Map<String,Object> getSiftData(String label,String key,String type,List<Map<String,String>> data){
//		Map<String,Object> reMap = new HashMap<String,Object>();
//		reMap.put("label",label);
//		reMap.put("key",key);
//		reMap.put("type",type);
//		if(CollectionUtils.isNotEmpty(data)){
//			reMap.put("data",data);
//		}
//		return reMap;
//	}
    /**
     * @params [label, key, type, data]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @description:筛选联动条件
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
    public static Map<String,Object> getSiftData(String label,String key,String type,String url,String value,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>();
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("value",value);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    /**
     * @params [label, key, type, data]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @description:筛选联动条件(同比上面方法，返回value字段改为defaultValue字段)
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
    public static Map<String,Object> getSiftDataDefaul(String label,String key,String type,String url,String defaultValue,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>();
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("defaultValue",defaultValue);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    public static Map<String,Object> getSiftData(String label,String key,String type,
                                                 String url,String value,String placeholder,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>();
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("value",value);
        reMap.put("placeholder",placeholder);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    /**
     * 按照前端标准格式封装
     * @param label
     * @param key
     * @param type
     * @param url
     * @param defaultValue
     * @param placeholder
     * @param mix
     * @param max
     * @param data
     * @return
     */
    public static Map<String,Object> getSifDateAll(String label,String key,String type,
                                                   String url,String defaultValue,String placeholder,Long mix,Long max,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>(9);
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("defaultValue",defaultValue);
        reMap.put("mix",mix);
        reMap.put("max",max);
        reMap.put("placeholder",placeholder);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    public static Map<String,Object> getLinkSiftData(String label,String key,String type,String url,String value,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>();
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("value",value);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    /**
     * 封装查询参数
     * @param label
     * @param key
     * @param type
     * @param url
     * @param defaultValue
     * @param data
     * @return
     */
    public static Map<String,Object> getLinkSiftDataDefaul(String label,String key,String type,String url,String defaultValue,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>();
        reMap.put("label",label);
        reMap.put("key",key);
        reMap.put("type",type);
        reMap.put("url",url);
        reMap.put("defaultValue",defaultValue);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        return reMap;
    }

    /**
     * @params [label, value]
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @description:下拉框
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
    public static Map<String,String> getSelectData(String label,String value){
        Map<String,String> reMap = new HashMap<>();
        reMap.put("label",label);
        reMap.put("value",value);
        return reMap;
    }
    /**
     * @params [label, value]
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @description:联动下拉框
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/8/21
     */
    public static Map<String,Object> getLinkSelectData(String label,String value,int hasChild){
        Map<String,Object> reMap = new HashMap<>();
        reMap.put("label",label);
        reMap.put("value",value);
        reMap.put("hasChild",hasChild);
        return reMap;
    }

    public static Map<String,Object> getLinkSelectData(String label, String value){
        Map<String,Object> reMap = new HashMap<>();
        reMap.put("label",label);
        reMap.put("value",value);
        return reMap;
    }

    /**
     * @params []
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description:组装查询服务搜索条件
     * @author hdf
     * @version 1.0
     * @since 1.0 2018/9/11
     */
    public static List<Map<String,Object>> assembleData(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(date);
        List<Map<String,Object>> siftData = new ArrayList<>();
        List<Map<String,Object>> selectData1 = new ArrayList<Map<String,Object>>();
        int start = Integer.valueOf(year)-8;
        int end = Integer.valueOf(year)+8;
        for(int i=start;i<=end;i++){
            String label = String.valueOf(i)+"-"+String.valueOf(i+1);
            selectData1.add(ComUtil.getLinkSelectData(label,String.valueOf(i),0));
        }
        siftData.add(ComUtil.getSiftData("学年","xn","select","",year,selectData1));
        List<Map<String,Object>> selectData2 = new ArrayList<Map<String,Object>>();
        selectData2.add(ComUtil.getLinkSelectData("全部","",0));
        selectData2.add(ComUtil.getLinkSelectData("1","3",0));
        selectData2.add(ComUtil.getLinkSelectData("2","12",0));
        siftData.add(ComUtil.getSiftData("学期","xq","select","","",selectData2));
        return siftData;
    }

    /**
     * 组装多选下拉框
     * @param type 多选默认：selects
     * @param key 筛选条件中对应的key值
     * @param label 文本框的标题
     * @param placeholder 文本框的展位提示符
     * @param defaultValue 初始化选中的值，为对应value的字符串数组
     * @param data 下拉数组（value为选中的值，label为显示对应的下拉内容）
     * @return
     */
    public static Map<String,Object> getsiftSelects(String type,String key,String label,String placeholder,String[] defaultValue,List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>(6);
        reMap.put("type",type);
        reMap.put("key",key);
        reMap.put("label",label);
        reMap.put("placeholder",placeholder);
        reMap.put("defaultValue",defaultValue);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data",Collections.EMPTY_LIST);
        }
        //因前端要求返回数组
        List<Map<String, Object>> siftData = new ArrayList<>();
        siftData.add(reMap);

        Map<String,Object> map = new HashMap<String,Object>(1);
        map.put("siftData",siftData);

        return map;
    }

    /**
     * 组装多选下拉框(无siftData层)
     * @param type 多选默认：selects
     * @param key 筛选条件中对应的key值
     * @param label 文本框的标题
     * @param placeholder 文本框的展位提示符
     * @param defaultValue 初始化选中的值，为对应value的字符串数组
     * @param data 下拉数组（value为选中的值，label为显示对应的下拉内容）
     * @return
     */
    public static Map<String,Object> getSelects(String type, String key, String label, String placeholder, String[] defaultValue, List<Map<String,Object>> data){
        Map<String,Object> reMap = new HashMap<String,Object>(6);
        reMap.put("type",type);
        reMap.put("key",key);
        reMap.put("label",label);
        reMap.put("placeholder",placeholder);
        reMap.put("defaultValue",defaultValue);
        if(CollectionUtils.isNotEmpty(data)){
            reMap.put("data",data);
        }else{
            reMap.put("data", Collections.EMPTY_LIST);
        }

        return reMap;
    }
}
