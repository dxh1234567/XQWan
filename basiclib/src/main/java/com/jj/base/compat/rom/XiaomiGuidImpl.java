package com.jj.base.compat.rom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import com.jj.base.utils.ApplicationUtils;
import com.jj.base.utils.SystemProperties;

/**
 * Created by yangxl on 2017/7/19.
 */

class XiaomiGuidImpl extends DefaultRomImpl {
    private static final String TAG = "XiaomiGuidImpl";

    @Override
    public boolean isShowInitSetting() {
        return true;
    }

    @Override
    public int getSystemType() {
        if (systemType == SYSTEM_TYPE_INVALIDE) {
            String miui = SystemProperties.get("ro.miui.ui.version.name", "").toLowerCase();
            if (!TextUtils.isEmpty(miui) && miui.length() >= 2) {
                String miuiVer = miui.substring(0, 2);
                if (miuiVer.compareTo("v8") >= 0) {
                    systemType = SYSTEM_TYPE_MIUI_V8;
                } else if (miuiVer.contains("v7")) {
                    systemType = SYSTEM_TYPE_MIUI_V7;
                } else if (miuiVer.contains("v6")) {
                    systemType = SYSTEM_TYPE_MIUI_V6;
                } else if (miuiVer.compareTo("v5") >= 0) {
                    systemType = SYSTEM_TYPE_MIUI_V5;
                } else {
                    systemType = SYSTEM_TYPE_MIUI_BL_V5;
                }
            }
        }
        return systemType;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        boolean res = false;
        if (systemType == SYSTEM_TYPE_MIUI_V5
                || systemType == SYSTEM_TYPE_MIUI_BL_V5) {
            res = startAutoStartV5(context);
        } else if (systemType >= SYSTEM_TYPE_MIUI_V6) {
            res = startAutoStartV6(context);
        }
        return res;
    }

    private boolean startAutoStartV5(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info;
        Intent intent;
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
            intent.putExtra("extra_package_uid", info.applicationInfo.uid);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(SCHEME, context.getPackageName(), null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //TODO 文案确认
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }

        intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //TODO 文案确认
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startAutoStartV6(Context context) {
        boolean result;
        Intent intent = new Intent("miui.intent.action.LICENSE_MANAGER");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.MainAcitivty");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        if (!result) {
            intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //TODO 文案确认
            result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        }
        return result;
    }

    @Override
    public void goToMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.xiaomi.market");
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    @Override
    public boolean startRunningAppsSetting(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {

            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.android.settings",
                    "com.android.settings.applications.ManageApplicationsActivity");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            intent.putExtra("com.android.settings.APPLICATION_LIST_TYPE", 2);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
                return true;
            }
        }
        return super.startRunningAppsSetting(context);
    }

    @Override
    public boolean startFloatWindowPermissionManager(Context context) {
        if(systemType == SYSTEM_TYPE_MIUI_BL_V5){
            return super.startFloatWindowPermissionManager(context);
        }
        if (!startPermissionManager(context)) {
            return super.startFloatWindowPermissionManager(context);
        }
        return true;
    }

    private boolean startPermissionManagerV8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }

        intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startPermissionManagerV6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }

        intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    private boolean startPermissionManagerV5(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(SCHEME, context.getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ApplicationUtils.startActivityForResultSafely(context, intent, 0)) {
            return true;
        }
        //TODO 文案确认
        intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    @Override
    public boolean startPermissionManager(Context context) {
        boolean res = false;
        if (systemType == SYSTEM_TYPE_MIUI_V5 ||
                systemType == SYSTEM_TYPE_MIUI_BL_V5) {
            res = startPermissionManagerV5(context);
        } else if (systemType == SYSTEM_TYPE_MIUI_V6 ||
                systemType == SYSTEM_TYPE_MIUI_V7) {
            res = startPermissionManagerV6(context);
        } else if (systemType == SYSTEM_TYPE_MIUI_V8) {
            res = startPermissionManagerV8(context);
        }
        if (!res) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            res = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        }
        return res;
    }
}
