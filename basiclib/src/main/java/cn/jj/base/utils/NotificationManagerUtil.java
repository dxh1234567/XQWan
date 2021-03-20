package cn.jj.base.utils;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

/**
 * Created by yangxl on 2017/11/13.
 */

public class NotificationManagerUtil {

    public static void cancel(Context context, int id) {
        try {
            NotificationManagerCompat.from(context).cancel(id);
        } catch (Exception e) {
            //@note bugly#2849  部分手机可能发生空指针异常：NotificationManager::sService为空
            e.printStackTrace();
        }
    }

    public static boolean notify(Context context, int id, Notification notification) {
        try {
            NotificationManagerCompat.from(context).notify(id, notification);
            return true;
        } catch (Exception e) {
            //@note bugly#2849  部分手机可能发生空指针异常：NotificationManager::sService为空
            e.printStackTrace();
        }
        return false;
    }
}
