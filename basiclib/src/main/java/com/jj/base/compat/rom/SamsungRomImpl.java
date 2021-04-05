package com.jj.base.compat.rom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.jj.base.utils.ApplicationUtils;

import static com.jj.base.utils.ApplicationUtils.isIntentAvailable;

/**
 * Created by yangxl on 2017/7/19.
 */

class SamsungRomImpl extends DefaultRomImpl {
    private static final String TAG = "SamsungRomImpl";

    private static final String SAMSUNG_SMART_MANAGER_PKG = "com.samsung.android.sm";
    private static final String SAMSUNG_SMART_MANAGER_CLASS = "com.samsung.android.sm.ui.ram.RamActivity";
    private static final String SAMSUNG_MEMORY_MANAGER_PKG = "com.samsung.memorymanager";
    private static final String SAMSUNG_MEMORY_MANAGER_CLASS = "com.samsung.memorymanager.RamActivity";
    private boolean hasSmartManager = isHaveSmartManager();
    private boolean hasMomoryManager = isHaveMomoryManager();

    @Override
    public boolean isShowInitSetting() {
        return (Build.VERSION.SDK_INT >= 21) &&
                hasSmartManager || hasMomoryManager;
    }

    @Override
    public int getSystemType() {
        return SYSTEM_TYPE_SAMSUNG;
    }

    @Override
    public boolean startAutoStartSetting(Context context) {
        boolean res = false;
        if (hasSmartManager) {
            res = startSmartManager(context);
        } else if (hasMomoryManager) {
            res = startMemoryManager(context);
        }
        return res;
    }

    private boolean startSmartManager(Context context) {
        boolean result;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(SAMSUNG_SMART_MANAGER_PKG, SAMSUNG_SMART_MANAGER_CLASS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
        if (!result) {
            intent = context.getPackageManager().getLaunchIntentForPackage(SAMSUNG_SMART_MANAGER_PKG);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                result = ApplicationUtils.startActivityForResultSafely(context, intent, 0);
            }
        }
        return result;
    }

    private boolean startMemoryManager(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(SAMSUNG_MEMORY_MANAGER_PKG, SAMSUNG_MEMORY_MANAGER_CLASS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }

    /**
     * 是否存在智能管理器的开机启动开关页面
     *
     * @return
     */
    public boolean isHaveSmartManager() {
        Intent intent = new Intent();
        intent.setClassName(SAMSUNG_SMART_MANAGER_PKG, SAMSUNG_SMART_MANAGER_CLASS);
        intent.setAction(Intent.ACTION_MAIN);
        return isIntentAvailable(intent);
    }

    /**
     * 是否存在内存管理的开机启动开关页面
     *
     * @return
     */
    public boolean isHaveMomoryManager() {
        Intent intent = new Intent();
        intent.setClassName(SAMSUNG_MEMORY_MANAGER_PKG, SAMSUNG_MEMORY_MANAGER_CLASS);
        intent.setAction(Intent.ACTION_MAIN);
        return isIntentAvailable(intent);
    }

    @Override
    public void goToMarket(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
        intent.setData(Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=" + context.getPackageName()));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }
}
