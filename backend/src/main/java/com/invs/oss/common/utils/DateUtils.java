package com.invs.oss.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    // 标准日期格式。
    public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取今日所在月份的简写
     *
     * @return
     */
    public static String getMonthEngLish() {
        try {
            Integer month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            Date date = sdf.parse(month.toString());
            sdf = new SimpleDateFormat("MMM", Locale.US);
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取今日日期
     *
     * @return
     */
    public static Date getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;
        try {
            time = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取今日开始时间
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
//        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 将日期格式Date转换成字符串
     *
     * @return 转换失败就返回null。
     */
    public static String getTodayDateString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int computeIntervalMonths(Date startDate, Date endDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        int year_s = c.get(Calendar.YEAR);
        int month_s = c.get(Calendar.MONTH);
        c.setTime(endDate);
        int year_e = c.get(Calendar.YEAR);
        int month_e = c.get(Calendar.MONTH);

        int result;
        if (year_e == year_s) {
            result = month_e - month_s;
        } else {
            result = 12 * (year_e - year_s) + month_e - month_s;
        }
        return result;
    }

    /**
     * @param startDate
     * @param endDate
     * @return 返回两个时间的差值天数
     */
    public static int computeIntervalDays(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "startDate and end Date can't be null!");
        }
        return (int) Math.round((endDate.getTime() - startDate.getTime()) / (1000d * 60 * 60 * 24));

    }

    /**
     * 将日期格式字符串转换成Date对象
     *
     * @param dateformate 需要转换的格式 例如：yyyy-MM-dd HH:mm:ss
     * @return 转换失败就返回null。
     */
    public static Date StringToDate(String dateformate, String source) {
        if (source == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat(dateformate);
            try {
                return df.parse(source);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 将日期转换成String
     *
     * @param dateformate 需要转换的格式 例如：yyyy-MM-dd HH:mm:ss
     * @return 转换失败就返回null。
     */
    public static String DateToString(String dateformate, Date source) {
        if (source == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat(dateformate);
            return df.format(source);
        }
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param sysDateTime 当前时间 yyyy-MM-dd HH:mm:ss
     * @param startTime   开始时间 yyyy-MM-dd HH:mm:ss
     * @param endTime     结束时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static boolean isInDate(Date sysDateTime, Date startTime, Date endTime) {
        if (sysDateTime.before(startTime) || sysDateTime.after(endTime)) {
            return false;
        }
        return true;
    }

    /**
     * 获取传过来时间 的前一天时间
     *
     * @param dNow 传过来时间
     * @return
     */
    public static Date getBeforeDate(Date dNow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        Date dBefore = calendar.getTime();   //得到前一天的时间
       /* String defaultStartDate=  DateToString("yyyy-MM-dd HH:mm:ss",dBefore);*/
        return dBefore;
    }

    /**
     * 获取参数月后的时间
     *
     * @param month 传过来月数
     * @return
     */
    public static Date getAfterMonthDate(Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.MONTH, month);
        Date dBefore = calendar.getTime();
       /* String defaultStartDate=  DateToString("yyyy-MM-dd HH:mm:ss",dBefore);*/
        return dBefore;
    }

    /**
     * 获取传过来时间 的前一天时间
     *
     * @param dNow    传过来时间
     * @param minutes 延时多久分钟
     * @return
     */
    public static Date getAfterMinuteDate(Date dNow, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.MINUTE, minutes);  //设置为前一天
        Date dAfter = calendar.getTime();   //得到前一天的时间
        return dAfter;
    }


    /**
     * 获取当天所在周的开始日期
     *
     * @return
     */
    public static String getTodayInWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        //所在周开始日期
        String date = DateToString("yyyy-MM-dd", cal.getTime());
        return date;
    }

    /**
     * 获取当天所在月的开始日期
     *
     * @return
     */
    public static String getTodayInMonthStartDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = DateToString("yyyy-MM-dd", c.getTime());
        return first;
    }


    /**
     * 获取本周第一天的日期
     *
     * @return
     */
    public static String getStartDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //获取本周一的日期
        String date = df.format(cal.getTime());
        System.out.println(date);
        return date;
    }

    /**
     * 获取上周第一天的日期
     *
     * @return
     */
    public static String getStartDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        int n = -1;
        cal.add(Calendar.DATE, n * 7);
        //想周几，这里就传几Calendar.MONDAY（TUESDAY...）
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        System.out.println(monday);
        return monday;
    }

    /**
     * 获取上月  格式：年-月
     *
     * @return
     */
    public static String getLastMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        //获取前月的第一天
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String firstDay = format.format(cal_1.getTime());
        return firstDay;
    }

    /**
     * 判断连续签到的天数
     *
     * @return
     */
    public static Integer getSerialDays(List<String> dates) {
        Integer days = 0;
        Date date = new Date();
        for (int i = 0; i < dates.size(); i++) {
            String d = DateToString("yyyy-MM-dd", date);
            if (dates.contains(d)) {
                days += 1;
                date = getBeforeDate(date);
            } else {
                break;
            }
        }
        return days;
    }




    /**
     * 取某天零点的函数
     *
     * @param date
     * @return
     */
    public static String getStartTimeofDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(day.getTime());
    }


    /**
     * 取某天末点的函数
     *
     * @param date
     * @return
     */
    public static String getEndTimeofDay(Date date) {
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 23);
        day.set(Calendar.MINUTE, 59);
        day.set(Calendar.SECOND, 59);
        day.set(Calendar.MILLISECOND, 999);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(day.getTime());
    }

    /**
     * 获取前某天
     * @param date
     * @return
     */
    public static Date getNextDay(Date date,int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -num);
        date = calendar.getTime();
        return date;
    }

    public static Date getTime(Date d,int hour,int minute,int second){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
//        System.out.println(getEndTimeofDay(getNextDay(new Date(),1)));
//        System.out.println(getStartTimeofDay(getNextDay(new Date(),1)));
    }


}
