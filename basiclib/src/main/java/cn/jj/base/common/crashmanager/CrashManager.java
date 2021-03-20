package cn.jj.base.common.crashmanager;

import android.content.Context;

import cn.jj.base.utils.LogUtil;


/**
 * Created by yangxl on 2017/11/20.
 */

public class CrashManager implements Thread.UncaughtExceptionHandler {
    static CrashManager mAppCrashHandler;
    private static final long DAY = 4 * 60 * 60 * 1000;
    private static final int MAX_CRASH_PRE_DAY = 3;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context;

    CrashManager(Context context) {
        this.context = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO: 2017/12/25
//        if (shouldRestart()) {
//            PendingIntent restartIntent;
//            if (Utility.sIsService) {
//                Intent intent = new Intent(context, EasyTouchService.class);
//                restartIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            } else {
//                Intent intent = new Intent(context, ClientMainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            }
//            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
//    }
        StackTraceElement[] stack = ex.getStackTrace();
        StringBuilder builder = new StringBuilder(1024);
        if (stack != null && stack.length > 0) {
            for (int i = 0; i < stack.length; i++) {
                builder.append(stack[i].toString() + "\n");
            }
        }
        LogUtil.e("CrashManager", builder.toString());
        LogUtil.appenderClose();
        if (mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
    }

    // TODO: 2017/12/25
//    private boolean shouldRestart() {
//        long currentTime = System.currentTimeMillis();
//        if (Utility.sIsService) {
//            int times = ServiceConfigModel.getInstance().getCrashTimes();
//            long firstCrashTime = ServiceConfigModel.getInstance().getFirstCrashTime();
//            ServiceConfigModel.getInstance().setCrashTimes(1 + times);
//            if (times < MAX_CRASH_PRE_DAY && firstCrashTime + DAY >= currentTime) {
//                return true;
//            } else if (firstCrashTime + DAY < currentTime) {
//                ServiceConfigModel.getInstance().setFirstCrashTime(currentTime);
//                ServiceConfigModel.getInstance().setCrashTimes(0);
//            }
//        } else {
//            int times = CommonConfigureModel.getInstance().getCrashTimes();
//            long firstCrashTime = CommonConfigureModel.getInstance().getFirstCrashTime();
//            CommonConfigureModel.getInstance().setCrashTimes(1 + times);
//            if (times < MAX_CRASH_PRE_DAY && firstCrashTime + DAY >= currentTime) {
//                return true;
//            } else if (firstCrashTime + DAY < currentTime) {
//                CommonConfigureModel.getInstance().setFirstCrashTime(currentTime);
//                CommonConfigureModel.getInstance().setCrashTimes(0);
//            }
//        }
//        return false;
//    }

    public static void init(Context context) {
        mAppCrashHandler = new CrashManager(context);
    }
}
