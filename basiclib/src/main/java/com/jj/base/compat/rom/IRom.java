package com.jj.base.compat.rom;

import android.content.Context;

/**
 * Created by yangxl on 2017/7/19.
 */


public interface IRom {
    String SCHEME = "package";

    int SYSTEM_TYPE_INVALIDE = -1;

    int SYSTEM_TYPE_EMUI_UNKNOWN = 100;
    int SYSTEM_TYPE_EMUI_1_0 = 110;
    int SYSTEM_TYPE_EMUI_1_5 = 115;
    int SYSTEM_TYPE_EMUI_1_6 = 116;
    int SYSTEM_TYPE_EMUI_2 = 120;
    int SYSTEM_TYPE_EMUI_3 = 130;
    int SYSTEM_TYPE_EMUI_3_OVER = 131;

    int SYSTEM_TYPE_MIUI_BL_V5 = 200;
    int SYSTEM_TYPE_MIUI_V5 = 201;
    int SYSTEM_TYPE_MIUI_V6 = 202;
    int SYSTEM_TYPE_MIUI_V7 = 203;
    int SYSTEM_TYPE_MIUI_V8 = 204;

    int SYSTEM_TYPE_MEIZU_UNKNOWN = 300;
    int SYSTEM_TYPE_MEIZU_V3 = 330;
    int SYSTEM_TYPE_MEIZU_V4 = 340;
    int SYSTEM_TYPE_MEIZU_V5 = 350;

    int SYSTEM_TYPE_SAMSUNG = 400;

    int SYSTEM_TYPE_OPPO_UNKNOWN = 500;
    int SYSTEM_TYPE_OPPO_V2 = 501;
    int SYSTEM_TYPE_OPPO_V3 = 502;

    int SYSTEM_TYPE_HTC = 600;

    int SYSTEM_TYPE_LG = 700;

    int SYSTEM_TYPE_SMARTISAN = 800;

    int SYSTEM_TYPE_VIVO = 900;

    int SYSTEM_TYPE_GOOGLE = 1000;

    boolean isShowInitSetting();

    int getSystemType();

    boolean startAutoStartSetting(Context context);

    boolean startProtectedApp(Context context);

    boolean startBatteryManager(Context context);

    void goToMarket(Context context);

    boolean startRunningAppsSetting(Context context);

    boolean startPowerSetting(Context context);

    boolean intoSystemSetting(Context context);

    boolean intoSystemDisplaySetting(Context context);

    boolean intoSystemSoundSetting(Context context);

    boolean intoSystemBlueToothSetting(Context context);

    boolean intoSystemRoamingSetting(Context context);

    boolean intoSystemWifiSetting(Context context);

    boolean intoSystemLocationSourceSetting(Context context);

    boolean intoSystemSyncSetting(Context context);

    boolean intoSystemAppDetailSetting(Context context, String packageName);

    boolean startFloatWindowPermissionManager(Context context);

    boolean startManageOverLayPermission(Context context);

    boolean startCalendar(Context context);

    boolean startCalculator(Context context);

    boolean startAlarmClock(Context context);

    boolean startAlbum(Context context);

    boolean startPermissionManager(Context context);

    boolean startWriteSettingsPermission(Context context);

    boolean startNotificationPolicyAccessSettings(Context context);
}