package com.neco.sglog.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 日期工具
 * @author ziyuan_deng
 * @date 2020/9/4
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private static final String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String SIMPLE_FORMAT = "yyyy-MM-dd";

    private static final String FORMAT = "yyyyMMddHHmmss";

    // SimpleDateFormat线程安全
    private static ThreadLocal<Map<String, SimpleDateFormat>> sdfMap = new ThreadLocal<Map<String, SimpleDateFormat>>() {

        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<String, SimpleDateFormat>();
        }
    };

    /**
     * 将日期转化为yyyy-MM-dd HH:mm:ss的字符串
     *
     * @param date
     * @return
     */
    public static String formatFullStr(Date date) {
        return null == date ? null : getSdf(FULL_FORMAT).format(date);
    }

    /**
     * 将日期转化为yyyyMMddHHmmss的字符串
     *
     * @param date
     * @return
     */
    public static String formatStr(Date date) {
        return null == date ? null : getSdf(FORMAT).format(date);
    }

    /**
     * 将日期转化为yyyy-MM-dd的字符串
     *
     * @param date
     * @return
     */
    public static String formatSimpleStr(Date date) {
        return null == date ? null : getSdf(SIMPLE_FORMAT).format(date);
    }


    /**
     * @param date
     * @param pattern
     * @return
     * @notes: 转string
     * @author: wei
     * @createTime: 2018年8月21日 上午10:00:38
     */
    public static String format(Date date, String pattern) {
        return null == date ? null : getSdf(pattern).format(date);
    }

    /**
     * @param dateStr
     * @param pattern
     * @return
     * @notes: 转date
     * @author: wei
     * @createTime: 2018年8月21日 上午10:01:06
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            return StringUtils.isEmpty(dateStr) ? null : getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            logger.debug(e.getMessage(), e);
            return null;
        }
    }

    private DateUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static String formatStrToFullFormatStr(String formatTimeStr) {
        if (StringUtils.isBlank(formatTimeStr)) {
            return null;
        }
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT);
        try {
            Date date = formater.parse(formatTimeStr);
            SimpleDateFormat df = new SimpleDateFormat(FULL_FORMAT);
            return df.format(date);
        } catch (Exception e) {
            logger.error("时间工具类将yyyyMMddHHmmss格式的时间字符串转换为yyyy-MM-dd HH:mm:ss格式的时间字符串发生异常", e);
            return null;
        }
    }

    public static String formatTimeToDate(long value) {
        if (value == 0){
            return "-";
        }
        Double time = Math.floor(value / 1000);
        Double day = Math.floor(time / (3600 * 24));
        Double hour = Math.floor((time - day * 3600 * 24) / 3600);
        Double minute = Math.floor((time - day * 3600 * 24 - hour * 3600) / 60);
        Double second = Math.floor(time - day * 3600 * 24 - hour * 3600 - minute * 60);
        String str = "";
        if (day > 0) {
            str += day.intValue() + "天";
        }
        if (hour > 0) {
            str += hour.intValue() + "时";
        }
        if (minute > 0) {
            str += minute.intValue() + "分";
        }
        str += second.intValue() + "秒";
        return str;

    }

    private static SimpleDateFormat getSdf(final String pattern) {
        Map<String, SimpleDateFormat> tl = sdfMap.get();
        SimpleDateFormat sdf = tl.get(pattern);
        if (sdf == null) {
            logger.debug(Thread.currentThread().getName() + " put new sdf of pattern " + pattern + " to map");
            sdf = new SimpleDateFormat(pattern);
            tl.put(pattern, sdf);
        }
        return sdf;
    }

    /**
     * 得到 (Date类型的日期-N个月) 的日期字符串 减法
     *
     * @param date
     * @param month
     * @return
     */
    public static Date getSubtractMonth(Date date, int month) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MONTH, -month);
        return now.getTime();
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天前格式为yyyy-MM-dd的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static String getYMDDateBefore(Date d, int day) {
        Date beforeDate = getDateBefore(d, day);
        return format(beforeDate, SIMPLE_FORMAT);
    }

    /**
     * 判断两个时间的大小 是否前面那个小于或等于后面那个 格式 yyyy-MM-dd
     *
     * @param startStr
     * @param endStr
     * @return true为是 false为否
     */
    public static boolean judgeDateEqual(String startStr, String endStr) {
        Date startDate = stringToSimpleDate(startStr);
        Date endDate = stringToSimpleDate(endStr);
        if (startDate.before(endDate) || startDate.compareTo(endDate) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是同一天
     *
     * @param startStr
     * @param endStr
     * @return
     */
    public static boolean judgeSameDate(String startStr, String endStr) {
        Date startDate = stringToSimpleDate(startStr);
        Date endDate = stringToSimpleDate(endStr);
        if (startDate.compareTo(endDate) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 字符串日期转Date类型
     *
     * @param str
     * @return
     */
    public static Date stringToSimpleDate(String str) {
        if (StringUtils.isBlank(str)){
            return null;
        }
        try {
            SimpleDateFormat simPatter = new SimpleDateFormat(SIMPLE_FORMAT);
            return simPatter.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将string日期转换为指定格式的日期
     *
     * @param str
     * @param fomart
     * @return
     */
    public static String formatFullStrToFormat(String str, String fomart) {
        try {
            SimpleDateFormat formtter = new SimpleDateFormat(FULL_FORMAT);
            Date date = formtter.parse(str);
            SimpleDateFormat df = new SimpleDateFormat(fomart);
            return df.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将时间转换为时间戳
     *
     * @param dateTime
     * @return
     */
    public static long dateToStamp(String dateTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(dateTime);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 获取日期的月份
     *
     * @param date
     * @return
     */
    public static int getDateMonth(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        return now.get(Calendar.MONTH) + 1;
    }

    public static Date getWeekStartTime(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToSimpleDate(dateStr));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//周日开始
        return calendar.getTime();
    }

    public static Date getWeekEndTime(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToSimpleDate(dateStr));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);//周六结束
        return calendar.getTime();
    }

    /**
     * 得到一周后的日期
     *@author NIXQ
     * @param day
     * @return
     */
    public static Date getDateLater(Date day) {
        Calendar now = Calendar.getInstance();
        now.setTime(day);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + 7);
        return now.getTime();
    }

    /**
     * 得到符合时间周期的时间组
     * @author NIXQ
     * @param startTime
     * @param periodStartTime
     * @return
     */
    public static List<Date> getPeriodDate(List<Date> dateList, Date startTime, Date periodStartTime) {
        Date dateLater = getDateLater(startTime);
        long startTimeLong = dateLater.getTime();
        long periodStartLong = periodStartTime.getTime();
//       递归调用得到一组数据
        if (startTimeLong <=periodStartLong) {
            dateList.add(dateLater);
            getPeriodDate(dateList, dateLater, periodStartTime);
        }

        return dateList;
    }

    /**
     * 根据日期得到是第几自然周的方法
     * @author NIXQ
     * @param today
     * @return
     * @throws ParseException
     */
    public static int getWeekNo(LocalDate today) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayStr = today.format(fmt);
        Date todayDate = sdf.parse(todayStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(todayDate);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }


    /**
     * 根据指定日期得到前一天的日期
     * NIXQ
     * @param time
     * @return
     */
    public static String getLastDay(String time){
        SimpleDateFormat sdff= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date=null;
        try {
            date = sdff.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day=calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day-1);

        String lastDay = sdff.format(calendar.getTime());
        return lastDay;
    }


    /**
     * 日期转化为时间戳
     * @param dateTime
     * @return
     */
    public static long dateToStamps(String dateTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(dateTime);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }

    }


    public static List<Date> getPeriodDates(List<Date> dateList, Date startTime, Date periodStartTime) {
        Date dateLater = getLastDays(startTime);
        long startTimeLong = dateLater.getTime();
        long periodStartLong = periodStartTime.getTime();
//       递归调用得到一组数据
        if (startTimeLong <=periodStartLong) {
            dateList.add(dateLater);
            getPeriodDates(dateList, dateLater, periodStartTime);
        }
        return dateList;
    }

    /**
     * 根据指定日期得到后一天的日期Date格式
     * NIXQ
     * @param date
     * @return
     */
    public static Date getLastDays(Date date){
        //SimpleDateFormat sdff= new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day=calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day+1);
        Date lastDay = calendar.getTime();
        return lastDay;
    }

    /**
     * 得到时间戳数组
     * @param list
     * @return
     */
    public static List<Long> dateTo(List<Date> list){
        List<Long> dateLong = new ArrayList<>();
        for (Date date:list) {
            dateLong.add(date.getTime());
        }
        return dateLong;
    }

    /*public static void main(String[] args) {
        System.out.println(DateUtils.formatStr(new Date()));
    	System.out.println(DateUtils.judgeSameDate("2018-10-24","2018-10-23 16:00:57"));
    	System.out.println(DateUtils.judgeDateEqual("2018-10-24","2018-10-23 16:00:57"));
    	System.out.println(DateUtils.judgeDateEqual("2018-10-24","2018-10-24 16:00:57"));
    	System.out.println(DateUtils.judgeDateEqual("2018-10-24","2018-10-26 16:00:57"));
		System.out.println(DateUtils.formatFullStrToFormat("2018-10-26 16:00:57", "HH:mm"));
	}*/
}
