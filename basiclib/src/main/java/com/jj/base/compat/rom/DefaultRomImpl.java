package com.jj.base.compat.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import com.jj.base.utils.ApplicationUtils;

import static android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS;

/**
 * Created by yangxl on 2017/7/19.
 */

class DefaultRomImpl implements IRom {

    int systemType = SYSTEM_TYPE_INVALIDE;

    public DefaultRomImpl() {
        systemType = getSystemType();
    }

    protected CharSequence fromHtml(Context context, int resId) {
        return Html.fromHtml(context.getString(resId));
    }

    @Override
    public boolean isShowInitSetting() {
        return false;
    }

    @Override
    public int getSystemType() {
        return SYSTEM_TYPE_INVALIDE;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        return false;
    }

    @Override
    public boolean startProtectedApp(Context context) {
        return false;
    }

    @Override
    public boolean startBatteryManager(Context context) {
        return false;
    }

    @Override
    public void goToMarket(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    @Override
    public boolean startRunningAppsSetting(Context context) {
        try {
            Intent mIntent = new Intent();
            ComponentName comp = new ComponentName("com.android.settings",
                    "com.android.settings.RunningServices");
            mIntent.setComponent(comp);
            mIntent.setAction("android.intent.action.VIEW");
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mIntent);
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public boolean startPowerSetting(Context context) {
        try {
            String action = "android.intent.action.POWER_USAGE_SUMMARY";
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemDisplaySetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    //TODO adapt devices
    public boolean intoSystemSoundSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemBlueToothSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    //TODO 飞行模式、sim卡逻辑判断
    public boolean intoSystemRoamingSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemWifiSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemLocationSourceSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemSyncSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean intoSystemAppDetailSetting(Context context, String packageName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean startFloatWindowPermissionManager(Context context) {
        if (startManageOverLayPermission(context)) {
            return true;
        }
        return startPermissionManager(context);
    }

    public final boolean startManageOverLayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        }
        return false;
    }

    public boolean startCalendar(Context context) {
        for (String app : AppConstant.CALENDAR_APPS) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(app);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startCalculator(Context context) {
        for (String app : AppConstant.CALCULATOR_APPS) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(app);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startAlarmClock(Context context) {
        for (String app : AppConstant.ALARMCLOCK_APPS) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(app);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startAlbum(Context context) {
        for (String app : AppConstant.GALLERY_APPS) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(app);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                    return true;
                }
            }
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    public boolean startPermissionManager(Context context) {
        return intoSystemAppDetailSetting(context, context.getPackageName());
    }

    public boolean startWriteSettingsPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    public boolean startNotificationPolicyAccessSettings(Context context) {
        Intent intent = new Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }
}
