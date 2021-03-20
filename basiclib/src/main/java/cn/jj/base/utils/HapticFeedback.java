package cn.jj.base.utils;

import android.content.Context;
import android.os.Vibrator;


/**
 * 自定义振动处理类
 *
 * @author liyan
 */
public class HapticFeedback {

    private static final long DURATION = 15;

    public static final int NO_REPEAT = -1;

    public static final long[] ONEVIBRATOR = new long[]{0, 2 * DURATION};

    public static void oneVibrate(Context context, boolean isControl) {
         if (!isControl) {
            try {
                Vibrator mVibrator = (Vibrator) context
                        .getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(ONEVIBRATOR, NO_REPEAT);
            } catch (SecurityException e) {
                //bugly#3955 某些山寨手机权限已申明，但还是会报Requires VIBRATE permission
            }
        }
    }
}
