package cn.jj.base.compat.rom;

import android.content.Context;
import android.os.Build;

/**
 * Created by yangxl on 2017/7/19.
 */

public class RomCompat {

    public static final int ROM_TYPE_OTHER = 0;
    public static final int ROM_TYPE_HTC = 1;
    public static final int ROM_TYPE_SAMSUNG = 2;
    public static final int ROM_TYPE_XIAOMI = 3;
    public static final int ROM_TYPE_OPPO = 4;
    public static final int ROM_TYPE_EMUI = 5;
    public static final int ROM_TYPE_MEIZU = 6;
    public static final int ROM_TYPE_VIVO = 7;
    public static final int ROM_TYPE_LG = 8;
    public static final int ROM_TYPE_SMARTISAN = 9;
    public static final int ROM_TYPE_GOOGLE = 10;

    private static final IRom IMPL;
    private static final int romType;

    static {
        String BRAND = Build.BRAND.toLowerCase();
        if (BRAND.contains("htc")) {
            IMPL = new HTCRomImpl();
            romType = ROM_TYPE_HTC;
        } else if (BRAND.contains("samsung")) {
            IMPL = new SamsungRomImpl();
            romType = ROM_TYPE_SAMSUNG;
        } else if (BRAND.contains("xiaomi")) {
            IMPL = new XiaomiGuidImpl();
            romType = ROM_TYPE_XIAOMI;
        } else if (BRAND.contains("oppo")) {
            IMPL = new OppoRomImpl();
            romType = ROM_TYPE_OPPO;
        } else if (BRAND.contains("huawei") || BRAND.contains("honor")) {
            IMPL = new EmuiRomImpl();
            romType = ROM_TYPE_EMUI;
        } else if (BRAND.contains("meizu")) {
            IMPL = new MeizuRomImpl();
            romType = ROM_TYPE_MEIZU;
        } else if (BRAND.contains("vivo")) {
            IMPL = new VivoRomImpl();
            romType = ROM_TYPE_VIVO;
        } else if (BRAND.contains("lg")) {
            IMPL = new LgRomImpl();
            romType = ROM_TYPE_LG;
        } else if (BRAND.contains("smartisan")) {
            IMPL = new SmartisanRomImpl();
            romType = ROM_TYPE_SMARTISAN;
        } else if (BRAND.contains("google")) {
            IMPL = new GoogleRomImpl();
            romType = ROM_TYPE_GOOGLE;
        } else {
            IMPL = new DefaultRomImpl();
            romType = ROM_TYPE_OTHER;
        }
    }

    public static boolean isShowInitSetting() {
        return IMPL.isShowInitSetting();
    }

    public static int getSystemType() {
        return IMPL.getSystemType();
    }

    public static boolean startAutoStartSetting(Context context) {
        return IMPL.startAutoStartSetting(context);
    }

    public static boolean startProtectedApp(Context context) {
        return IMPL.startProtectedApp(context);
    }

    public static boolean startBatteryManager(Context context) {
        return IMPL.startBatteryManager(context);
    }

    public static int getRomType() {
        return romType;
    }

    public static void goToMarket(Context context) {
        IMPL.goToMarket(context);
    }

    public static boolean startRunningAppsSetting(Context context) {
        return IMPL.startRunningAppsSetting(context);
    }

    public static boolean startPowerSetting(Context context) {
        return IMPL.startPowerSetting(context);
    }

    public static boolean intoSystemSetting(Context context) {
        return IMPL.intoSystemSetting(context);
    }

    public static boolean intoSystemDisplaySetting(Context context) {
        return IMPL.intoSystemDisplaySetting(context);
    }

    public static boolean intoSystemSoundSetting(Context context) {
        return IMPL.intoSystemSoundSetting(context);
    }

    public static boolean intoSystemBlueToothSetting(Context context) {
        return IMPL.intoSystemBlueToothSetting(context);
    }

    public static boolean intoSystemRoamingSetting(Context context) {
        return IMPL.intoSystemRoamingSetting(context);
    }

    public static boolean intoSystemWifiSetting(Context context) {
        return IMPL.intoSystemWifiSetting(context);
    }

    public static boolean intoSystemLocationSourceSetting(Context context) {
        return IMPL.intoSystemLocationSourceSetting(context);
    }

    public static boolean intoSystemSyncSetting(Context context) {
        return IMPL.intoSystemSyncSetting(context);
    }

    public static boolean intoSystemAppDetailSetting(Context context, String packageName) {
        return IMPL.intoSystemAppDetailSetting(context, packageName);
    }

    public static boolean startFloatWindowPermissionManager(Context context) {
        return IMPL.startFloatWindowPermissionManager(context);
    }

    public static boolean startCalendar(Context context) {
        return IMPL.startCalendar(context);
    }

    public static boolean startCalculator(Context context) {
        return IMPL.startCalculator(context);
    }

    public static boolean startAlarmClock(Context context) {
        return IMPL.startAlarmClock(context);
    }

    public static boolean startAlbum(Context context) {
        return IMPL.startAlbum(context);
    }

    public static boolean startPermissionManager(Context context) {
        return IMPL.startPermissionManager(context);
    }

    public static boolean startWriteSettingsPermission(Context context) {
        return IMPL.startWriteSettingsPermission(context);
    }

    public static boolean startNotificationPolicyAccessSettings(Context context) {
        return IMPL.startNotificationPolicyAccessSettings(context);
    }
}
