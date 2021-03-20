package cn.jj.base.compat.rom;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import cn.jj.base.utils.ApplicationUtils;
import cn.jj.base.utils.SystemUtil;

/**
 * Created by yangxl on 2017/7/19.
 */

class MeizuRomImpl extends DefaultRomImpl {

    @Override
    public boolean isShowInitSetting() {
        return systemType > SYSTEM_TYPE_MEIZU_UNKNOWN;
    }

    @Override
    public int getSystemType() {
        String display = SystemUtil.getBuildDisplay();
        if (systemType == SYSTEM_TYPE_INVALIDE) {
            if (display.contains("flymeos3")) {
                systemType = SYSTEM_TYPE_MEIZU_V3;
            } else if (display.contains("flymeos4")) {
                systemType = SYSTEM_TYPE_MEIZU_V4;
            } else if (display.contains("flyme5")) {
                systemType = SYSTEM_TYPE_MEIZU_V5;
            } else {
                systemType = SYSTEM_TYPE_MEIZU_UNKNOWN;
            }
        }
        return systemType;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        boolean res = false;
        if (systemType == SYSTEM_TYPE_MEIZU_V4 ||
                systemType >= SYSTEM_TYPE_MEIZU_V5) {
            res = startAutoStartTry1(context);
        } else if (systemType == SYSTEM_TYPE_MEIZU_V3) {
            res = startAutoStartTry2(context);
        }
        if (!res) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            res = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        }
        return res;
    }

    @Override
    public boolean startProtectedApp(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        String className = systemType >= SYSTEM_TYPE_MEIZU_V5 ?
                "com.meizu.safe.SecurityMainActivity" : "com.meizu.safe.SecurityCenterActivity";
        intent.setClassName("com.meizu.safe", className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    @Override
    public boolean startFloatWindowPermissionManager(Context context) {
        if (!startAutoStartSetting(context)) {
            return super.startFloatWindowPermissionManager(context);
        }
        return true;
    }

    @Override
    public boolean startPermissionManager(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartTry1(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartTry2(Context context) {
        Intent intent = new Intent("android.settings.APP_OPS_SETTINGS");
        intent.setClassName("com.android.settings", "com.android.settings.Settings$AppControlSettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }
}
