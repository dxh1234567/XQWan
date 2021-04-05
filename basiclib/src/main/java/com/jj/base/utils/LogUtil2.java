package com.jj.base.utils;

import android.util.Log;

import java.util.ArrayList;

import com.jj.logger.Logger;

/**
 * Log记录类
 */
public class LogUtil2 {

    public static final String TAG = "MyLog";
    public static boolean isWriteFile = true;
    public static boolean isDebug = true;

    public static void i(String TAG, String msg) {
        Log.i(TAG, msg);
        if (isWriteFile) {
            Logger.t(TAG).i(msg);
        }
    }

    public static void d(String TAG, String msg) {
        Log.d(TAG, msg);
        if (isWriteFile) {
            Logger.t(TAG).d(msg, (Object[]) null);
        }
    }

    public static void e(String TAG, String msg, Exception e) {
        Log.e(TAG, msg, e);
        if (isWriteFile) {
            Logger.t(TAG).e(e, msg);
        }
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
        if (isWriteFile) {
            Logger.t(TAG).e(msg);
        }
    }

    public static String listToStr(ArrayList<?> list) {
        StringBuilder str = new StringBuilder("{");
        for (int i = 0; i < list.size(); i++) {
            str.append(list.get(i).toString()).append(",");
        }
        str.append("}");
        return str.toString();
    }



}
