package cn.jj.base.common.view;

import android.content.Context;

import cn.jj.basiclib.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 生成wheel的各种选项
 * Created by yangxl on 2016/10/23.
 */
public class WheelStyle {
    private static final int MAX_YEAR;
    private static final int MAX_MONTH;
    private static final int MAX_DAY;
    private static final int MIN_YEAR;

    static {
        Calendar calendar = Calendar.getInstance();
        MAX_YEAR = calendar.get(Calendar.YEAR);
        MAX_MONTH = calendar.get(Calendar.MONTH);
        MAX_DAY = calendar.get(Calendar.DAY_OF_MONTH);
        MIN_YEAR = MAX_YEAR - 80;
    }

    public static final int STYLE_NONE = 0;
    /**
     * Wheel Style Hour
     */
    public static final int STYLE_HOUR = 1;
    /**
     * Wheel Style Minute
     */
    public static final int STYLE_MINUTE = 2;
    /**
     * Wheel Style Year
     */
    public static final int STYLE_YEAR = 3;
    /**
     * Wheel Style Month
     */
    public static final int STYLE_MONTH = 4;
    /**
     * Wheel Style Day
     */
    public static final int STYLE_DAY = 5;
    /**
     * Wheel Style TEXT
     */
    public static final int STYLE_TEXT = 6;

    private WheelStyle() {
    }

    public static List<String> getItemList(int Style, int... values) {
        if (Style == STYLE_HOUR) {
            return createHourString();
        } else if (Style == STYLE_MINUTE) {
            return createMinuteString();
        } else if (Style == STYLE_YEAR) {
            return createYearString();
        } else if (Style == STYLE_MONTH) {
            if (null != values && values.length == 1) {
                return createMonthString(values[0]);
            } else {
                return createMonthString(MAX_YEAR);
            }
        } else if (Style == STYLE_DAY) {
            if (null != values && values.length == 2) {
                return createDayString(values[0], values[1]);
            } else {
                return createDayString(MAX_MONTH, MAX_YEAR);
            }
        } else if (Style == STYLE_TEXT) {
            throw new IllegalArgumentException("style_text not support getItemList");
        } else {
            throw new IllegalArgumentException("style is illegal");
        }
    }

    private static List<String> createHourString() {
        List<String> wheelString = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            wheelString.add(String.format("%02d", i));
        }
        return wheelString;
    }

    private static List<String> createMinuteString() {
        List<String> wheelString = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            wheelString.add(String.format("%02d", i));
        }
        return wheelString;
    }

    private static List<String> createYearString() {
        List<String> wheelString = new ArrayList<>();
        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            wheelString.add(Integer.toString(i));
        }
        return wheelString;
    }

    private static List<String> createMonthString(int year) {
        int maxMonth = year >= MAX_YEAR ? MAX_MONTH + 1 : 12;
        List<String> wheelString = new ArrayList<>();
        for (int i = 1; i <= maxMonth; i++) {
            if (i > 9) {
                wheelString.add(String.format("%02d", i));
            } else {
                wheelString.add(String.format("%d", i));
            }
        }
        return wheelString;
    }

    private static List<String> createDayString(int month, int year) {
        year = Math.max(MIN_YEAR, Math.min(year, MAX_YEAR));
        if (year == MAX_YEAR) {
            month = Math.min(month, MAX_MONTH);
        }
        int days;
        if (year == MAX_YEAR && month == MAX_MONTH) {
            days = MAX_DAY;
        } else {
            days = getDaysInMonth(month, year);
        }
        List<String> wheelString = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            if (i > 9) {
                wheelString.add(String.format("%02d", i));
            } else {
                wheelString.add(String.format("%d", i));
            }
        }
        return wheelString;
    }

    public static String getUnitString(Context context, int wheelStyle) {
        if (wheelStyle == STYLE_HOUR) {
            return context.getResources().getString(R.string.basic_date_hour);
        } else if (wheelStyle == STYLE_MINUTE) {
            return context.getResources().getString(R.string.basic_date_minute);
        } else if (wheelStyle == STYLE_YEAR) {
            return context.getResources().getString(R.string.basic_date_year);
        } else if (wheelStyle == STYLE_MONTH) {
            return context.getResources().getString(R.string.basic_date_month);
        } else if (wheelStyle == STYLE_DAY) {
            return context.getResources().getString(R.string.basic_date_day);
        } else if (wheelStyle == STYLE_TEXT) {
            return "";
        } else {
            throw new IllegalArgumentException("style is illegal");
        }
    }

    private static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }
}
