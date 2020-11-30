package com.hyy.accountassis.util;


import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.hyy.accountassis.app.MyApp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */

public class CommonUtils {

    /**
     * The date of type string into a date
     *
     * @param dateStr To the time of conversion
     * @param format  To format conversion
     * @return
     */
    public static Date strToDate(String dateStr, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date dateFront = null;
        try {
            dateFront = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFront;
    }

    /**
     * date
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static boolean isNullString(@Nullable String str) {
        return str == null || str.length() == 0 || "null".equals(str);
    }

    public static String simpleDateFormat(String format, Date date) {
        if (isNullString(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        String content = (new SimpleDateFormat(format)).format(date);
        return content;
    }

    /**
     * Long time conversion time format
     *
     * @param longTime
     * @param format
     * @return
     */
    public static String longToDate(long longTime, String format) {
        Date date = new Date(longTime);
        SimpleDateFormat sd = new SimpleDateFormat(format);
        return sd.format(date);
    }

    /**
     * Long time conversion time format
     *
     * @param longTime
     * @param format
     * @return
     */
    public static Date long2Date(long longTime, String format) {
        Date date = new Date(longTime);
        SimpleDateFormat sd = new SimpleDateFormat(format);
        try {
            return sd.parse(sd.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getCurrentDateTime(String format) {
        return simpleDateFormat(format, new Date());
    }


    public static int dip2px(float dpValue) {
        return dp2px(dpValue);
    }

    public static int dp2px(float dpValue) {
        float scale = MyApp.getInstance().getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        return px2dp(pxValue);
    }

    public static int px2dp(float pxValue) {
        float scale = MyApp.getInstance().getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static int sp2px(float spValue) {
        float fontScale = MyApp.getInstance().getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    public static int px2sp(float pxValue) {
        float fontScale = MyApp.getInstance().getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5F);
    }
}
