package com.efrobot.salespromotion.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/11/22.
 */

public class TimeUtil {
    /**
     * 将位置信息转换成时间表示
     */
    private static final String TIME_FORMAT = "%02d′%02d″";
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.CHINA);

    public static String dateTransformTime(float date) {
        int minute = (int) (date / 60);
        int second = (int) (date % 60);
        String time = String.format(TIME_FORMAT, minute, second);
        //Log.i(TAG, "time=" + time);
        return time;

    }

    public static long data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                Locale.CHINA);
        Date date;
        long l = 0;
        try {
            date = sdr.parse(time);
            l = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public static String getDateToStringByFormat(long time, int type) {
        Date d = new Date(time);
        String format = "";
        switch (type) {
            case 0:
                format = "yyyy-MM-dd-HH-mm";
                break;

        }
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.CHINESE);
        return sf.format(d);
    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timesOne(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;

    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param time
     * @return
     */
    /* 时间戳转换成字符窜 */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINESE);
        return sf.format(d);
    }

    public static long getTime() {
        Calendar calendar = Calendar.getInstance();
        long unixTime = calendar.getTimeInMillis();//这是时间戳
        return unixTime;
    }

    public static String getTime2(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public static List<String> getTimerArray(String content) {
        List<String> time = new ArrayList<>();
        time.add(content.substring(0, content.lastIndexOf("年")));
        time.add(content.substring(content.lastIndexOf("年") + 1, content.lastIndexOf("月")));
        time.add(content.substring(content.lastIndexOf("月") + 1, content.lastIndexOf("日")));
        time.add(content.substring(content.lastIndexOf("日") + 1, content.lastIndexOf("时")));
        time.add(content.substring(content.lastIndexOf("时") + 1, content.lastIndexOf("分")));
        return time;
    }

    public static int getMinute() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return mCalendar.get(Calendar.MINUTE);
    }

    public static int getHour24() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public static String getHour12() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return String.valueOf(mCalendar.get(Calendar.HOUR));
    }

    public static String getYear() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return String.valueOf(mCalendar.get(Calendar.YEAR));
    }

    public static String getMonth() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return String.valueOf(mCalendar.get(Calendar.MONTH) + 1);
    }

    public static String getDay() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
