package cn.jj.base.compat.rom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import cn.jj.base.utils.ApplicationUtils;

/**
 * Created by yangxl on 2017/8/24.
 */

public class LgRomImpl extends DefaultRomImpl {

    @Override
    public int getSystemType() {
        return SYSTEM_TYPE_LG;
    }

    @Override
    public boolean startPermissionManager(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }
}
