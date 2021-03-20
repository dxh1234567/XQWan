package cn.jj.base.compat.rom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.jj.base.utils.ApplicationUtils;
import cn.jj.base.utils.SystemProperties;


/**
 * Created by yangxl on 2017/7/19.
 */

class EmuiRomImpl extends DefaultRomImpl {
    private static final String TAG = "EmuiRomImpl";

    @Override
    public boolean isShowInitSetting() {
        return systemType > SYSTEM_TYPE_EMUI_1_6;
    }

    @Override
    public int getSystemType() {
        if (systemType == SYSTEM_TYPE_INVALIDE) {
            String sysVersion = SystemProperties.get("ro.build.version.emui", "").toLowerCase();
            if (!TextUtils.isEmpty(sysVersion)) {
                if (sysVersion.compareTo("emotionui_3.0") > 0) {
                    systemType = SYSTEM_TYPE_EMUI_3_OVER;
                } else if (sysVersion.equals("emotionui_3.0")) {
                    systemType = SYSTEM_TYPE_EMUI_3;
                } else if (sysVersion.startsWith("emotionui_2")) {
                    systemType = SYSTEM_TYPE_EMUI_2;
                } else if (sysVersion.startsWith("emotionui_1.5")) {
                    systemType = SYSTEM_TYPE_EMUI_1_5;
                } else if (sysVersion.startsWith("emotionui_1.6")) {
                    systemType = SYSTEM_TYPE_EMUI_1_6;
                } else if (sysVersion.startsWith("emotionui_1.0")) {
                    systemType = SYSTEM_TYPE_EMUI_1_0;
                } else {
                    systemType = SYSTEM_TYPE_EMUI_UNKNOWN;
                }
            }
        }
        return systemType;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        boolean res;
        if (systemType == SYSTEM_TYPE_EMUI_1_5) {
            res = startAutoStartFor1_5(context);
        } else {
            res = startAutoStartForOther(context);
        }
        return res;
    }

    @Override
    public boolean startProtectedApp(Context context) {
        boolean res;
        if (systemType == SYSTEM_TYPE_EMUI_1_5) {
            res = startAutoStartFor1_5(context);
        } else {
            res = startProtectedAppForOther(context);
        }
        return res;
    }

    @Override
    public boolean startFloatWindowPermissionManager(Context context) {
        if (!startManageOverLayPermission(context)) {
            if (SYSTEM_TYPE_EMUI_3 == systemType) {
                Intent intent = new Intent();
                intent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
            } else {
                Intent intent = new Intent();
                intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.mainscreen.MainScreenActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                    return true;
                }
                intent = context.getPackageManager().getLaunchIntentForPackage("com.huawei.systemmanager");
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean startPermissionManager(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startProtectedAppForOther(Context context) {
        Intent intent = new Intent("huawei.intent.action.HSM_PROTECTED_APPS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartFor1_5(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.powersavingmode", "com.huawei.powersavingmode.PowerSavingModeActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartForOther(Context context) {
        Intent intent = new Intent("huawei.intent.action.HSM_BOOTAPP_MANAGER");
        intent.setPackage("com.huawei.systemmanager");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }
        intent = context.getPackageManager().getLaunchIntentForPackage("com.huawei.systemmanager");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                return true;
            }
        }
        return false;
    }
}
