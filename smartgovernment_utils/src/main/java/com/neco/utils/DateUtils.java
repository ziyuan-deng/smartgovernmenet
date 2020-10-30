package com.neco.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import lombok.experimental.var;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日期工具
 * 
 * @author ziyuan_deng
 * @date 2019年11月7日 下午3:15:49
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
	 * @param date
	 * @return
	 */
	public static String formatFullStr(Date date) {
		return null == date ? null : getSdf(FULL_FORMAT).format(date);
	}

	/**
	 * 将日期转化为yyyy-MM-dd的字符串
	 * @param date
	 * @return
	 */
	public static String formatSimpleStr(Date date) {
		return null == date ? null : getSdf(SIMPLE_FORMAT).format(date);
	}

	/**
	 * @notes: 转string
	 * @author: wei
	 * @createTime: 2018年8月21日 上午10:00:38
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return null == date ? null : getSdf(pattern).format(date);
	}

	/**
	 * @notes: 转date
	 * @author: wei
	 * @createTime: 2018年8月21日 上午10:01:06
	 *
	 * @param dateStr
	 * @param pattern
	 * @return
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
			SimpleDateFormat df = new SimpleDateFormat();
			return df.format(date);
		} catch (Exception e) {
			logger.error("时间工具类将yyyyMMddHHmmss格式的时间字符串转换为yyyy-MM-dd HH:mm:ss格式的时间字符串发生异常", e);
			return null;
		}
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
	 * 得到几天后的日期
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}
	/**
	 * 得到几分钟后的时间
	 * @param minute
	 * @return
	 */
	public static Date getMinuteAfter( int minute) {
		Calendar now = Calendar.getInstance();
		//now.setTime(d);
		//now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		now.add(Calendar.MINUTE, minute);
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

	public static Date stringToFullDate(String str) {
		if (StringUtils.isBlank(str)){
			return null;
		}
		try {
			SimpleDateFormat simPatter = new SimpleDateFormat(FULL_FORMAT);
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
        	SimpleDateFormat formtter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date date = formtter.parse(str);
            SimpleDateFormat df = new SimpleDateFormat(fomart);
            return df.format(date);
        } catch (Exception e) {
            return "-";
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
            SimpleDateFormat simpleDateFormat = getSdf(FULL_FORMAT);
            Date date = simpleDateFormat.parse(dateTime);
            return date.getTime();
    	} catch (Exception e) {
            return 0;
        }
        
    }

	/**
	 * 时间戳转日期
	 * @ziyuan_deng
	 * @param dateTime
	 * @return
	 */
	public static String stampToDate(long dateTime) {
    	try {
            SimpleDateFormat simpleDateFormat = getSdf(SIMPLE_FORMAT);
            Date date = new Date(dateTime);
			String day = simpleDateFormat.format(date);
            return day;
    	} catch (Exception e) {
            return null;
        }

    }

    
    /**
     * 获取日期的月份
     * @ziyuan_deng
     * @param date
     * @return
     */
    public static int getDateMonth(Date date) {
    	Calendar now = Calendar.getInstance();
        now.setTime(date);
        return now.get(Calendar.MONTH) + 1;
    }

	/**
	 * 获取日期的年份
	 * @ziyuan_deng
	 * @param date
	 * @return
	 */
	public static int getDateYear(Date date) {
    	Calendar now = Calendar.getInstance();
        now.setTime(date);
        return now.get(Calendar.YEAR);
    }

	/**
	 * 根据日期 找到对应日期的 星期
	 * @ziyuan_deng
	 */
	public static String getDayOfWeekByDate(String date) {
		String dayOfweek = "-1";
		try {
			SimpleDateFormat myFormatter = getSdf(SIMPLE_FORMAT);
			Date myDate = myFormatter.parse(date);
			SimpleDateFormat formatter = new SimpleDateFormat("E");
			String str = formatter.format(myDate);
			dayOfweek = str;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return dayOfweek;
	}


	//根据两个日期计算两者的天数
	public static int daysBetween(Date now, Date returnDate)
	{
		Calendar cNow = Calendar.getInstance();
		Calendar cReturnDate = Calendar.getInstance();
		cNow.setTime(now);
		cReturnDate.setTime(returnDate);
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);
		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		return millisecondsToDays(intervalMs);
	}
	public static void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}
	public static int millisecondsToDays(long intervalMs) {
		return (int) (intervalMs / (1000 * 86400));
	}

	/**
	 * 获取某个日期所在的周的星期一是什么日期
	 * @ziyuan_deng
	 * @param time
	 * @return
	 */
	public static Date getMonday(Date time) {
		SimpleDateFormat sdf = getSdf(SIMPLE_FORMAT);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		//判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
		//return sdf.format(cal.getTime());
		return cal.getTime();
	}

	/**
	 * @ziyuan_deng
	 * @param time
	 * @return
	 */
	public static String getMondayStr(Date time) {
		SimpleDateFormat sdf = getSdf(SIMPLE_FORMAT);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		//判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取某个日期所在的周的星期日是什么日期
	 * @ziyuan_deng
	 * @param time
	 * @return
	 */
	public static String getSundayStr(Date time) {
		SimpleDateFormat sdf = getSdf(SIMPLE_FORMAT);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		//判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		//获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		//获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		//根据日历的规则
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
		cal.add(Calendar.DATE, 6);
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取某个日期所在周的星期天
	 * @ziyuan_deng
	 * @param time
	 * @return
	 */
	public static Date getSunday(Date time) {
		SimpleDateFormat sdf = getSdf(SIMPLE_FORMAT);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		//判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		//获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		//获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		//根据日历的规则
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);
		cal.add(Calendar.DATE, 6);
		//return sdf.format(cal.getTime());
		return  cal.getTime();
	}

	/**
	 * 计算两个日期之间的时间差（以天为单位）
	 * @ziyuan_deng
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long until(LocalDate startDate, LocalDate endDate){

		return startDate.until(endDate, ChronoUnit.DAYS);

	}

	/**
	 * 计算某一日期与今天的间隔时间差（以天为时间单位）
	 * @ziyuan_deng
	 * @param endDate
	 * @return
	 */
	public static long until(LocalDate endDate){

		return LocalDate.now().until(endDate, ChronoUnit.DAYS);

	}

	/**
	 * 两个日期之间间隔的周数
	 * @ziyuan_deng
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static  long  DateInterval(LocalDate startDate,LocalDate endDate){
        return until(startDate,endDate)/7+1;
	}
	/**
	 * 两个日期之间间隔的周数
	 * @ziyuan_deng
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static  long  DateInterval(Date startDate,Date endDate){
		Instant instant = startDate.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalDate startTime = localDateTime.toLocalDate();
		Instant instant2 = endDate.toInstant();
		ZoneId zone2 = ZoneId.systemDefault();
		LocalDateTime localDateTime2 = LocalDateTime.ofInstant(instant2, zone2);
		LocalDate endTime = localDateTime2.toLocalDate();

		return until(startTime,endTime)/7+1;
	}

	/**
	 * 获取某月的天数
	 * @ziyuan_deng
	 * @param year
	 * @param month
	 * @return
	 */
	public static Integer daysCount(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 0);
		return cal.get(Calendar.DATE);
	}

	/**
	 * 根据日期获取当天是周几
	 * @author ziyuan_deng
	 * @param datetime 日期
	 * @return 周几
	 */
	public static String dateToWeek(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
		Date date;
		try {
			date = sdf.parse(datetime);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDays[w];
	}

	/**
	 * 对应的日期是星期几
	 * @param datetime
	 * @return
	 */
	public static String dateToWeek(Date datetime) {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(datetime);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDays[w];
	}

    /**
     * 返回去年的总天数
     * @param today
     * @return
     */
	public static int getlastYearDayCount(Date today){
        int year = getDateYear(today);
        int lastYear = year -1;
        if(lastYear%4==0 && lastYear%100!=0){
            return 366;
        }else
        if(lastYear%400==0){
            return 366;
        }else {
            return 365;
        }
    }

	/*public static void main(String[] args) {
		*//*String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		List<String> weekendList = Arrays.asList(weekDays[0],weekDays[6]);
		List<String> dutyDateList = Arrays.asList(weekDays[1], weekDays[2], weekDays[3], weekDays[4], weekDays[5]);
		Date dateBefore = DateUtils.getDateBefore(new Date(), 54);
		String nowXn = DateUtils.formatSimpleStr(dateBefore);
		System.out.println(weekendList);
		System.out.println(dutyDateList);*//*

		Date date = getMinuteAfter(-12);
		String str = formatFullStr(date);
		System.out.println(str);
	}*/


}
