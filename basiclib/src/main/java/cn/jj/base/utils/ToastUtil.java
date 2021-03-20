package cn.jj.base.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.SoftReference;

import cn.jj.base.common.ThreadManager;
import cn.jj.base.common.view.toast.ToastCompat;


/**
 * Toast统一管理类
 */
public class ToastUtil {

    private static SoftReference<Toast> mToast;

    /**
     * 短时间显示Toast
     */
    public static void showShort(final CharSequence message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_SHORT);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI, () -> show(message, Toast.LENGTH_SHORT));
        }
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(final Context context, final CharSequence message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_SHORT);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI, () -> show(message, Toast.LENGTH_SHORT));
        }
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(final int message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_SHORT);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI,
                    () -> show(message, Toast.LENGTH_SHORT));
        }
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(final Context context, final int message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_SHORT);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI,
                    () -> show(message, Toast.LENGTH_SHORT));
        }
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(final Context context, final CharSequence message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_LONG);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI,
                    () -> show(message, Toast.LENGTH_LONG));
        }
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(final Context context, final int message) {
        if (ThreadManager.runningOn(ThreadManager.THREAD_UI)) {
            show(message, Toast.LENGTH_LONG);
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI, () -> show(message, Toast.LENGTH_LONG));
        }
    }

    /**
     * 自定义显示Toast时间
     */
    private static void show(CharSequence message, int duration) {
        try {
            cancel();
            Toast toast = ToastCompat.makeText(Utility.getApplication(), message, duration);
            toast.setText(message);
            toast.setDuration(duration);
            toast.show();
            mToast = new SoftReference<>(toast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义显示Toast时间
     */
    private static void show(int message, int duration) {
        try {
            cancel();
            Toast toast = ToastCompat.makeText(Utility.getApplication(), message, duration);
            toast.setText(message);
            toast.setDuration(duration);
            toast.show();
            mToast = new SoftReference<>(toast);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void cancel() {
        if (mToast != null && mToast.get() != null) {
            mToast.get().cancel();
        }
    }

}
