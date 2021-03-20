package cn.jj.base.utils;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 系统Intent的工具类
 *
 * @author wanglc
 */
public class SystemIntent {

    /**
     * 启动浏览器
     *
     * @param context
     * @param url
     */
    public static boolean startBrowser(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @SuppressLint("InlinedApi")
    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR, -1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        Collections.sort(usageStatsList, (lhs, rhs) -> {
            if (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) {
                return -1;
            }
            if (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) {
                return 0;
            }
            return 1;
        });
        return usageStatsList;
    }

    public static boolean isHome(Context context, Intent baseIntent) {

        String packagename = baseIntent.getComponent().getPackageName();

        return getHomes(context).contains(packagename);
    }

    public static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            //三星S6/谷歌5X，快速切换功能无法切换手机系统“设置”页面
            if (!ri.activityInfo.packageName.equals("com.android.settings")) {
                names.add(ri.activityInfo.packageName);
            }
        }
        return names;
    }

    public static boolean isAvilible(Context context, ComponentName componentName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager

        try {
            /**
             * 当componentName 是自己这个app时候 会抛出异常，so过滤掉自己
             */
            if ("ComponentInfo{com.shere.easytouch/com.shere.easytouch.module.main.view.activity.ClientMainActivity}".equals(componentName.toString())) {
                //Log.e("lijinbao", "componentName  等于"+componentName.toString());
                return false;
            }
            ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);
            if (activityInfo == null) {
                return false;
            }
            CharSequence s = activityInfo.applicationInfo.loadLabel(packageManager);
            String rts = s.toString();
            if (rts != null) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }


    //// TODO: 2017/8/4  remove to ApplicationUtil
    public static boolean isSystemapp(Context context, String appPackage) {

        try {
            Intent i = context.getPackageManager()
                    .getLaunchIntentForPackage(appPackage);
            return i == null;

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isGpsEnable(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isBluetoothEnable(Context context) {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothadapter == null)
            return false;
        int state = BluetoothAdapter.STATE_OFF;
        try {
            state = bluetoothadapter.getState();
        } catch (SecurityException e) {
            //bugly#4638 java.lang.SecurityException Need BLUETOOTH permission: Neither user 10111 nor current process has android.permission.BLUETOOTH.
            //@note 部分机型android.permission.BLUETOOTH相关权限打包丢失，导致崩溃
        }
        return (state == BluetoothAdapter.STATE_ON
                || state == BluetoothAdapter.STATE_TURNING_ON);
    }

    public static void setBluetoothStatus(Context context, boolean enable) {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        if (null != bluetoothadapter) {
            if (enable)
                bluetoothadapter.enable();
            else
                bluetoothadapter.disable();
        }
    }

    public static boolean isRotationEnable(Context context) {
        int state = Settings.System.getInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
        return 1 == state;
    }

    public static void setRotationEnable(Context context, boolean enable) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, enable ? 1 : 0);
    }

    public static boolean isAirplaneEnable(Context context) {
        int state;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                state = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON);
            } else {
                state = Settings.Global.getInt(context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON);
            }
        } catch (Exception e) {
            state = 0;
        }
        return 1 == state;
    }


    public static boolean isAutoBrightnessEnable(Context context) {
        boolean automicBrightness = false;

        try {
            automicBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return automicBrightness;
    }

    public static void setAutoBrightnessEnable(Context context, boolean enable) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, enable ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        if (isAutoBrightnessEnable(context)) {
            try {
                float value = Settings.System.getFloat(context.getContentResolver(), "screen_auto_brightness_adj");
                nowBrightnessValue = (int) ((value + 1) * 255 / 2f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                nowBrightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nowBrightnessValue;
    }


    public static void setScreentimeout(Context context, int timeout) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getScreentimeout(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
