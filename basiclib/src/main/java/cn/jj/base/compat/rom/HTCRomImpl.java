package cn.jj.base.compat.rom;

import android.content.Context;
import android.content.Intent;
import cn.jj.base.utils.ApplicationUtils;

/**
 * Created by yangxl on 2017/7/19.
 */

class HTCRomImpl extends DefaultRomImpl {
    @Override
    public boolean isShowInitSetting() {
//        return AppInfoModel.getInstance().isAppExisted("com.toolwiz.batterymaster.htc");
        return false;
    }

    @Override
    public int getSystemType() {
        return SYSTEM_TYPE_HTC;
    }

    @Override
    public boolean startBatteryManager(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.toolwiz.batterymaster.htc", "com.toolwiz.batterymaster.ui.activity.BatteryMainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return ApplicationUtils.startActivityForResultSafely(context, intent, 0);
    }
}
