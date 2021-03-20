package cn.jj.base.utils;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by yangxl on 2017/12/25.
 */

public class Utility {

    public static final int NO_CACHE = 1;
    public static final int NO_STORE = 0x2;

    private static final int DEFAULT_TIMEOUT = 5;

    private static Application sApplication;
    private static WeakReference<Activity> currentActiveActivity;

    public static Application getApplication() {
        if (sApplication == null) {
            throw new RuntimeException(" Utility::init must be called in Application::onCreate()");
        }
        return sApplication;
    }

    public static void Init(Application application) {
        sApplication = application;
    }

    public static boolean isDebug = false;

    public static void setDebug(boolean isDebug) {
        Utility.isDebug = isDebug;
    }

    @Nullable
    public static Activity getCurrentActiveActivity() {
        if (currentActiveActivity == null) {
            return null;
        }
        return currentActiveActivity.get();
    }

    @Nullable
    public static void setCurrentActiveActivity(Activity activeActivity) {
        currentActiveActivity = new WeakReference<>(activeActivity);
    }
}