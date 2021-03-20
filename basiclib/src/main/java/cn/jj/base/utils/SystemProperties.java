package cn.jj.base.utils;

import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by yangxl on 2017/7/19.
 */

public class SystemProperties {
    private static Class<?> mClassType = null;
    private static Method mGetMethod = null;

    public  static String get(String key, String def) {
        init();
        String value;
        try {
            value = (String) mGetMethod.invoke(mClassType, key);
            if (TextUtils.isEmpty(value)) {
                value = def;
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = def;
        }
        if (TextUtils.isEmpty(value)) {
            value = def;
        }
        return value;
    }

    private static void init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");
                mGetMethod = mClassType.getDeclaredMethod("get", String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}