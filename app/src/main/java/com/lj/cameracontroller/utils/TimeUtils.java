package com.lj.cameracontroller.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/28.
 * 时间工具类
 */

public class TimeUtils {
    private static String LOG_TAG = "TimeUtils";
    private static String defaultDatePattern = null;
    private static String timePattern = "HH:mm";
    public static final String TS_FORMAT = TimeUtils.getDatePattern()
            + " HH:mm:ss.S";
    public static final String SDF_FORMAT = "yyyy-MM-dd";
    public static final String SDF1_FORMAT = "HH:mm:ss";
    public static final String SDF2_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF5_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String SDF3_FORMAT = "yyyyMMddHHmmss";
    public static final String SDF4_FORMAT = "yyyy/MM/dd HH:mm";


    /**
     * 将指定日期按默认格式进行格式代化成字符串后输出如：yyyy-MM-dd
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * 将指定日期按默认格式进行格式代化成字符串后输出如：yyyy-M-d
     */
    public static final String getDate(Date aDate, String sMack) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(sMack);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * 将日期类转换成指定格式的字符串形式
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            Log.e(LOG_TAG, "aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }


    /**
     * 将日期字符串按指定格式转换成日期类型
     *
     * @param aMask
     *            指定的日期格式，如:yyyy-MM-dd
     * @param strDate
     *            待转换的日期字符串
     */

    public static final Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            Log.e(LOG_TAG, "ParseException: " + pe);
            throw pe;
        }
        return (date);
    }
    /**
     * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
     */
    public static String getDateTime() {
        try {
            Calendar cale = Calendar.getInstance();
            return new SimpleDateFormat(SDF2_FORMAT).format(cale.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取服务器当前时间，以格式为yyyyMMddHHmmss字符串形式返回
     *
     * @return
     */
    public static String getDateYMDHMS() {
        Calendar cale = Calendar.getInstance();
        return new SimpleDateFormat(SDF3_FORMAT).format(cale.getTime());
    }

    /**
     * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm的日期字符串形式返回
     */
    public static String getDateTime2() {
        try {
            Calendar cale = Calendar.getInstance();
            return new SimpleDateFormat(SDF5_FORMAT).format(cale.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得服务器当前日期，以格式为：yyyy-MM-dd的日期字符串形式返回
     */
    public static String getDate() {
        try {
            Calendar cale = Calendar.getInstance();
            return new SimpleDateFormat(SDF_FORMAT).format(cale.getTime());
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getDate():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获得服务器当前日期，以格式为：strFormat的日期字符串形式返回
     */
    public static String getDate(String strFormat) {
        try {
            Calendar cale = Calendar.getInstance();
            SimpleDateFormat sf = new SimpleDateFormat(strFormat);
            return sf.format(cale.getTime());
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getDate():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获得服务器当前时间，以格式为：HH:mm:ss的日期字符串形式返回
     */
    public static String getTime() {
        String temp = " ";
        try {
            Calendar cale = Calendar.getInstance();
            temp += new SimpleDateFormat(SDF1_FORMAT).format(cale.getTime());
            return temp;
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getTime():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获得服务器当前日期的年份
     */
    public static String getYear() {
        try {
            Calendar cale = Calendar.getInstance();
            return String.valueOf(cale.get(Calendar.YEAR));
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getYear():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获得服务器当前日期的月份
     */
    public static String getMonth() {
        try {
            Calendar cale = Calendar.getInstance();
            java.text.DecimalFormat df = new java.text.DecimalFormat();
            df.applyPattern("00;00");
            return df.format((cale.get(Calendar.MONTH) + 1));
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getMonth():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获得服务器在当前月中天数
     */
    public static String getDay() {
        try {
            Calendar cale = Calendar.getInstance();
            return String.valueOf(cale.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            Log.e(LOG_TAG, "DateUtil.getDay():" + e.getMessage());
            return "";
        }
    }

    /**
     * 获取当前日期是星期几（数字类）
     * @return
     */
    public static int getDayOfWeek() {
        try {
            Calendar cale = Calendar.getInstance();
            return cale.get(Calendar.DAY_OF_WEEK);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getDayOfWeek:" + e.getMessage());
            return Calendar.SUNDAY;
        }
    }

    /**
     * 获取当前日期是星期几（字符串型）
     * @return
     */
    public static String getWeek() {
        String showDate = "";
        switch (getDayOfWeek()) {
            case Calendar.SUNDAY:
                showDate = "星期日";
                break;
            case Calendar.MONDAY:
                showDate = "星期一";
                break;
            case Calendar.TUESDAY:
                showDate = "星期二";
                break;
            case Calendar.WEDNESDAY:
                showDate = "星期三";
                break;
            case Calendar.THURSDAY:
                showDate = "星期四";
                break;
            case Calendar.FRIDAY:
                showDate = "星期五";
                break;
            case Calendar.SATURDAY:
                showDate = "星期六";
                break;
        }
        return showDate;
    }


    /**
     * 返回默认的日期格式
     */
    public static synchronized String getDatePattern() {
        defaultDatePattern = "yyyy-MM-dd";
        return defaultDatePattern;
    }


    /**
     * 格式化时间
     *
     * @param date
     *            日期, 例如：20131129
     * @return 格式化后的日期 2013-11-29
     */
    public static String formatter(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return format2.format(format1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间
     *
     * @param str
     *            把yyyyMMddHHmmss转成yyyy-MM-dd HH:mm:ss格式
     * @return
     */
    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 格式化时间
     *
     * @param str
     *            把yyyyMMddHHmmss转成yyyy-MM-dd HH:mm格式
     * @return
     */
    public static String formatDate2(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 格式化时间
     *
     * @param str
     *            把yyyyMMddHHmmss转成yyyy-MM-dd格式
     * @return
     */
    public static String formatDateYMD(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     *
     * @param str
     *            把yyyy-MM-dd HH:mm 转成yyyy-MM-dd格式
     * @return
     */
    public static String formatDateYmd(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;

    }

    /**
     *
     * @param str
     *            把yyyy-MM-dd HH:mm 转成HH:mm格式
     * @return
     */
    public static String formatDateHm(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;

    }

    /**
     *
     * @param str
     *            把yyyy-MM-dd HH:mm 转成HH:mm格式
     * @return
     */
    public static String formatDateChinaDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;

    }

    // 一天的毫秒数 86400000 = 24*60*60*1000;
    private static final int millisPerDay = 86400000;
    // 一小时的毫秒数 3600000 = 24*60*60*1000;
    private static final int millisPerHour = 3600000;

    /**
     * 计算时间差 (时间单位,开始时间,结束时间) 调用方法
     * howLong("h","2007-08-09 10:22:26","2007-08-09 20:21:30") ///9小时56分 返回9小时
     * */
    public static long howLong(String unit, String time1, String time2)
            throws ParseException {
        // 时间单位(如：不足1天(24小时) 则返回0)，开始时间，结束时间
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time1);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time2);
        long ltime = date1.getTime() - date2.getTime() < 0 ? date2.getTime()
                - date1.getTime() : date1.getTime() - date2.getTime();
        if (unit.equals("s")) {
            return ltime / 1000;// 返回秒
        } else if (unit.equals("m")) {
            return ltime / 60000;// 返回分钟
        } else if (unit.equals("h")) {
            return ltime / millisPerHour;// 返回小时
        } else if (unit.equals("d")) {
            return ltime / millisPerDay;// 返回天数
        } else {
            return 0;
        }
    }
}
