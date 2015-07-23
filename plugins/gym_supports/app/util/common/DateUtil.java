package util.common;

import org.apache.commons.lang.StringUtils;
import play.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 日期工具类.
 * <p/>
 * User: sujie
 * Date: 4/6/12
 * Time: 3:57 PM
 */
public class DateUtil {

    // SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");


    public static Date getEndOfDay() {
        return getEndOfDay(new Date());
    }

    public static Date getEndOfDay(Date day) {
        if (day == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getBeginOfDay(Date day) {
        if (day == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    public static Date getBeginOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    /**
     * n天后的结束时间
     *
     * @return
     */
    public static Date getEndExpiredDate(int n) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, +n);
        Date date = DateUtil.getEndOfDay(cal.getTime());
        return date;
    }

    /**
     * n天后的开始时间
     *
     * @return
     */
    public static Date getBeginExpiredDate(int n) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, +n);
        Date date = DateUtil.getBeginOfDay(cal.getTime());
        return date;
    }

    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到本月的第一天
     *
     * @return
     */
    public static Date firstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    /**
     * 得到查询月份的第一天
     *
     * @return
     */
    public static Date firstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    /**
     * 获得指定日期的最后一天
     *
     * @param date
     * @return
     */
    public static Date lastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    /**
     * 判断是否还有7天到期
     *
     * @param date 截止日期
     * @return true false
     * @throws ParseException
     */

    public static boolean getDiffDate(Date date) {
        long dateRange = diffDay(date);
        if (dateRange == 7) {
            return true;
        }

        return false;
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static String getNowTime() {

        Calendar c = Calendar.getInstance();
        String nowTime = c.get(Calendar.MONTH) + 1 + "月" + c.get(Calendar.DATE) + "日" + c.get(Calendar.HOUR_OF_DAY) + "时" + c
                .get(Calendar.MINUTE) + "分";

        return nowTime;
    }

    public static Long diffDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long dateRange = 0l;
        String now = format.format(new Date());
        Date sysDate = null;
        try {
            sysDate = format.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateRange = date.getTime() - sysDate.getTime();
        long time = 1000 * 3600 * 24; //A day in milliseconds
        return dateRange / time;
    }


    /**
     * 日期转化成字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date, int days) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return format.format(cal.getTime());
    }

    public static String  dateToString2(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(date);
    }

    /**
     * 字符串转化成日期
     *
     * @param sDate         日期字符串
     * @param formatPattern 转化格式
     * @return
     */
    public static Date stringToDate(String sDate, String formatPattern) {
        if (StringUtils.isBlank(sDate)) {
            return null;
        }
        Date date = null;
        try {
            SimpleDateFormat formatDate = new SimpleDateFormat(formatPattern);
            date = formatDate.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转化成日期
     *
     * @param date         日期字符串
     * @param formatPattern 转化格式
     * @return
     */
    public static String dateToString(Date date, String formatPattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatDate = new SimpleDateFormat(formatPattern);
        return formatDate.format(date);
    }

    /**
     * 根据给定时间段和间隔天数，返回指定时间字符串列表
     *
     * @param beginAt
     * @param endAt
     * @param dateFormat
     * @return
     */
    public static List<String> getDateList(Date beginAt, Date endAt, int intervalDays, String dateFormat) {
        if (beginAt == null && endAt == null){
            return new ArrayList<>();
        }
        Date date = beginAt;
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        List<String> dateList = new ArrayList<>();
        long oneDay = 1000L * 60 * 60 * 24;  // 一天的时间
        do {
            dateList.add(df.format(date));
            date = new Date(date.getTime() + oneDay * intervalDays);
        } while (DateUtil.getBeginOfDay(date).compareTo(DateUtil.getBeginOfDay(endAt)) < 0);
        if (DateUtil.getBeginOfDay(date).compareTo(DateUtil.getBeginOfDay(endAt)) >= 0) {
            dateList.add(df.format(endAt));
        }
        return dateList;
    }

    /**
     * 根据给定时间段和间隔天数，返回指定时间字符串列表
     *
     * @param beginAt
     * @return
     */
    public static Date getDateAfterTimes(Date beginAt,int times) {
        Date date = new Date();
        if (beginAt != null){
            date = beginAt;
        }
        long timesDay = 1000L * 60 * times;  // 额外增加的时间   1000L * 60 秒 * 当前输入分钟数
        date = new Date(date.getTime() + timesDay);
        return date;
    }

    /**
     * 获取指定日期之前n天的日期.
     *
     * @param day
     * @param intervalDays
     * @return
     */
    public static Date getBeforeDate(Date day, int intervalDays) {
        Calendar beforeDay = Calendar.getInstance();
        beforeDay.setTime(day);
        beforeDay.add(Calendar.DATE, 1 - intervalDays);
        return beforeDay.getTime();
    }
    
    /**
     * @Title: getAfterDate 
     * @Description: 某一天后，指定的几天
     * @author YangShanCHeng
     * @return Date    返回类型 
     * @throws
     */
    public static Date getAfterDate(Date day,int afterDays){
    	 Calendar afterDay = Calendar.getInstance();
    	 afterDay.setTime(day);
    	 afterDay.add(Calendar.DATE, afterDays);
         return afterDay.getTime();
    }

    //获取下一年的日期
    public static Date nextYear(Date currentDate) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(currentDate);
        cal.add(GregorianCalendar.YEAR, 1);//在年上加1
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    //获取前月的第一天/
    public static Date monthOfFirstDay() {
        Calendar cal = Calendar.getInstance();//获取当前日期
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    //获取前月的第一天/
    public static Date lastMonthOfFirstDay() {
        Calendar cal = Calendar.getInstance();//获取当前日期
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    //获取前月的第一天/
    public static Date lastMonthOfFirstDay(Date date) {
        Calendar cal = Calendar.getInstance();//获取当前日期
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    //获取前月的最后一天
    public static Date lastMonthOfEndDay() {
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH, 0);
        cale.set(Calendar.HOUR_OF_DAY, 23);
        cale.set(Calendar.MINUTE, 59);
        cale.set(Calendar.SECOND, 59);
        return cale.getTime();

    }

    public static Date lastMonthOfEndDayByDate(Date date){
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        cale.set(Calendar.HOUR_OF_DAY, 23);
        cale.set(Calendar.MINUTE, 59);
        cale.set(Calendar.SECOND, 59);
        return cale.getTime();
    }

    public static Date lastMonthOfEndDayByDate(Date date , Integer number) {
        number = number == null ? 0 : number;
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        cale.add(Calendar.MONTH , number);
        return cale.getTime();
    }

    /**
     * 获取明天的0点的时间.
     *
     * @return
     */
    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取n个小时前的时间
     *
     * @param i 小时数
     * @return
     */
    public static Date getBeforeHour(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 0 - i);
        return calendar.getTime();
    }

    /**
     * 获取n个小时前的时间
     *
     * @param i 小时数
     * @return
     */
    public static Date getBeforeMinutes(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 0 - i);
        return calendar.getTime();
    }

    /**
     * 得到指定日期的星期数，返回数字范围为1至7，周日为1，周六为7
     * @param date 日期时间
     * @return 数字范围1-7
     */
    public static Integer getIntegerWeek(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        return cd.get(Calendar.DAY_OF_WEEK);
    }

	/**
	 * 根据传入日期查询是周几 已经转换成中国地区的周
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeek(Date date) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		int week = cd.get(Calendar.DAY_OF_WEEK);
		if (week == 1)
			return "周日";
		else if(week==2){
			return "周一";
		}else if(week==3)
			return "周二";
		else if(week==4)
			return "周三";
		else if(week==5)
			return "周四";
		else if(week==6)
			return "周五";
		else
			return "周六";
	}

    /**
     * 返回分钟的时间差
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long  differenceMinute(Date startDate , Date endDate) {
        if(startDate == null || endDate == null){
            return 0l;
        } else {
            long diff = endDate.getTime() - startDate.getTime() ;
            return diff / (60*1000) ;
        }
    }

    /**
     * 获取指定时间与现在时间的对比，显示几秒前/几分钟前/几天前/几周前
     * @return
     */
    public static String getDateDiffCompareNow(Date date){
        if(date == null){
            return "";
        }
        long diff = new Date().getTime()-date.getTime() ;
        diff =  diff/1000;
        Logger.info("-----------" + diff + "--------------date:" + date);
        if(diff<60*3){      //小于三分钟显示刚刚
            return "刚刚";
        }
        if(diff>=60*3&&diff<60*30){    //大于三分钟小于半小时显示 分钟数
           return diff/60+"分钟前";
        }
        if(diff>=60*30&&diff<60*60){
            return "半小时前";
        }
        if(diff>=60*60&&diff<24*60*60){
            return diff/60/60+"小时前";
        }
        if(diff>24*60*60&&diff<24*60*60*7){
            return diff/60/60/24+"天前";
        }
        return "一周前";
    }


    public static Weeks getWeeks(Date searchDate) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(searchDate);
        int week = cd.get(Calendar.DAY_OF_WEEK);
        if (week == 1)
            return Weeks.SUNDAY;
        else if(week==2){
            return Weeks.MONDAY;
        }else if(week==3)
            return Weeks.THURSDAY;
        else if(week==4)
            return Weeks.WEDNESDAY;
        else if(week==5)
            return Weeks.TUESDAY;
        else if(week==6)
            return Weeks.FRIDAY;
        else
            return Weeks.SATURDAY;
    }

    /**
     * 判断时间大小   如果 后面的比前面的大 true
     * @param smallDate
     * @param bigDate
     * @return
     */
    public static Boolean compareDate(Date smallDate , Date bigDate) {
        if(bigDate.getTime() > smallDate.getTime()){
            return true;
        } else {
            return false;
        }
    }
    
    public static void main(String[] args){
    	//Date toDate = DateUtil.getBeforeDate(new Date(), 1);
//    	Date toDate = DateUtil.getAfterDate(new Date(), 1);
//    	List<String> days = DateUtil.getDateList(new Date(), toDate, 1, "yyyy-MM-dd");
//    	for(String s : days){
//    		Logger.info(getWeek(DateUtil.stringToDate(s, "yyyy-MM-dd")));
//    	}
        Date startDate = DateUtil.stringToDate("2013-12-10 17:14:00" , "yyyy-MM-dd HH:mm:ss");
        Date endDate = new Date();
        differenceMinute(startDate , endDate);
        Logger.info("args = " + DateUtil.lastMonthOfEndDayByDate(new Date()));
    }
}
