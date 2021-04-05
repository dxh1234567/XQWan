package com.jj.base.compat.rom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.jj.base.utils.ApplicationUtils;
import com.jj.base.utils.SystemProperties;

/**
 * Created by yangxl on 2017/7/19.
 */

class OppoRomImpl extends DefaultRomImpl {
    private static final String TAG = "OppoRomImpl";

    public OppoRomImpl() {
    }

    @Override
    public boolean isShowInitSetting() {
        return systemType == SYSTEM_TYPE_OPPO_V2 || systemType == SYSTEM_TYPE_OPPO_V3;
    }

    @Override
    public int getSystemType() {
        if (systemType == SYSTEM_TYPE_INVALIDE) {
            String oppoName = SystemProperties.get("ro.build.version.opporom", "").toLowerCase();
            if (TextUtils.isEmpty(oppoName)) {
                systemType = SYSTEM_TYPE_OPPO_UNKNOWN;
            } else if (oppoName.contains("v2")) {
                systemType = SYSTEM_TYPE_OPPO_V2;
            } else if (oppoName.contains("v3")) {
                systemType = SYSTEM_TYPE_OPPO_V3;
            } else {
                systemType = SYSTEM_TYPE_OPPO_UNKNOWN;
            }
        }
        return systemType;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        boolean res;
        if (systemType == SYSTEM_TYPE_OPPO_V3) {
            res = startAutoStartForV3(context);
        } else {
            res = startAutoStartForV2(context);
        }
        return res;
    }

    private boolean startAutoStartForV2(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.SecureSafeMainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartForV3(Context context) {
        boolean result;
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.SecureSafeMainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        if (!result) {
            intent = new Intent("android.intent.action.MAIN");
            intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        }
        return result;
    }

    private boolean startProjectedAppForV2(Context context) {
        try {
            ApplicationUtils.showSystemRecentApps(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean startBatteryManager(Context context) {
        Intent intent = new Intent("com.coloros.action.powermanager");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    /**
     * @note OPPO手机比较特殊，无法在开启后弹出引导界面，只能在启动设置界面同时弹出引导界面,所以返回值都为false
     */
    @Override
    public boolean startFloatWindowPermissionManager(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        // OPPO A53|5.1.1|2.1
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }
        // OPPO R7s|4.4.4|2.1
        intent.setAction("com.color.safecenter");
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }
        intent.setAction("com.coloros.safecenter");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }
        return super.startFloatWindowPermissionManager(context);
    }

    @Override
    public boolean startPermissionManager(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        if (!ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return super.startPermissionManager(context);
        }
        return true;
    }

    @Override
    public boolean startProtectedApp(Context context) {
        if (systemType == SYSTEM_TYPE_OPPO_V3) {
            return startProjectedAppForV3(context);
        } else {
            return startProjectedAppForV2(context);
        }
    }

    private boolean startProjectedAppForV3(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
                || Build.VERSION.RELEASE.contains("5.0.2")) {
            try {
                ApplicationUtils.showSystemRecentApps(context);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
