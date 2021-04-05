package com.jj.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.Locale;

/**
 * 管理APPmaster相关信息类
 * eg:当前语言、地区、版本等等
 * Created by yangxl on 2016/12/12.
 */

public class AppEnvUtil {
    private static String sAndroidID;

    private static Integer sVersionCode;
    private static String sVersionName;
    private static String sChannel;


    public static String getAndroidID() {
        if (sAndroidID != null)
            return sAndroidID;
        try {
            sAndroidID = Settings.System.getString(
                    Utility.getApplication().getContentResolver(),
                    Settings.System.ANDROID_ID);
        } catch (Exception ex) {
        }
        return sAndroidID;
    }

    public static int getVersionCode() {
        if (sVersionCode != null)
            return sVersionCode;
        initVersionInfo();
        return sVersionCode;
    }

    public static String getVersionName() {
        if (sVersionName != null)
            return sVersionName;
        initVersionInfo();
        return sVersionName;
    }

    public static String getUmengChannel(Context context) {
        if (context == null || context.getPackageManager() == null) {
            return "baidu";
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getApplicationInfo().packageName, PackageManager.GET_META_DATA);

            Bundle metaData = applicationInfo == null ? null : applicationInfo.metaData;
            if (metaData != null) {
                String channel = metaData.getString("UMENG_CHANNEL");
                if (!TextUtils.isEmpty(channel)) {
                    return channel;
                }
            }
        } catch (Exception e) {
        }
        return "baidu";
    }


    private static synchronized void initVersionInfo() {
        if (sVersionCode != null)
            return;
        Context context = Utility.getApplication();
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(), 0);
            sVersionCode = info.versionCode;
            sVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public static int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getCountry() {
        Locale l = Locale.getDefault();
        String country = l.getCountry().toLowerCase();
        return country;
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage().toLowerCase();
        return language;
    }

    public static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage().toLowerCase();
        if (language.contains("zh")) {
            language = l.getCountry().toLowerCase();
        }
        return language;
    }

    public static boolean getAPPChannel() {
        try {
            Context context = Utility.getApplication();
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(
                            context.getApplicationInfo().packageName,
                            PackageManager.GET_META_DATA);

            Bundle metaData = applicationInfo.metaData;
            if (metaData != null) {
                String channel = metaData.getString("APP_CHANNEL");
                return !TextUtils.isEmpty(channel) && channel.equals("internal");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
