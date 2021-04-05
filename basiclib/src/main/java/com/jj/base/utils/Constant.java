package com.jj.base.utils;

import android.os.Build;

/**
 * Created by yangxl on 2017/12/25.
 */

public class Constant {

    public static final int ITEM_ICON_SIZE = ScreenUtil.dp2px(48);
    public static final int MIN_ITEM_ICON_SIZE = 48;

    public static final String AREA_DATA_FILE_NAME = "area.json";
    public static final Long DEFAULT_BIRTH_DATE =
            TypeConvertUtil.strToTime("yyyy-MM-dd", "1980-1-1");

    public static final String BIRTH_DATE_SEPARATOR = " ";

    public static final boolean ATLEAST_P = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    public static final boolean ATLEAST_O = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static final boolean ATLEAST_N = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

    public static final boolean ATLEAST_MARSHMALLOW = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    public static final boolean ATLEAST_LOLLIPOP_MR1 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;

    public static final boolean ATLEAST_LOLLIPOP =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static final boolean ATLEAST_KITKAT =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    public static final boolean ATLEAST_JB_MR1 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    public static final boolean ATLEAST_JB_MR2 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final boolean ATLEAST_JB =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
}
