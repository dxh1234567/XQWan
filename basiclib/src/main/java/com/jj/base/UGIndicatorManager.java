package com.jj.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.StringRes;

import java.util.WeakHashMap;

import com.jj.base.common.view.UGToast;
import com.jj.basiclib.R;

/**
 * Created By duXiaHui
 * on 2021/1/30
 */
public class UGIndicatorManager {
    private static Handler handler = new Handler();

    private static WeakHashMap<Activity, Dialog> dialogLruCache = new WeakHashMap<>(4);

    private static Dialog progressDialog;

    public static void showLoading(Activity activity) {
        showLoading(activity, false);
    }

    static public void dismissLoading() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 显示loading
     *
     * @param cancel 是否可以点击外部区域被取消
     */
    public static void showLoading(Activity activity, boolean cancel) {
        if (activity == null) {
            return;
        }
        // 如果当前有正在显示的loading，隐藏
        dismissLoading();
        // 缓存
        progressDialog = dialogLruCache.get(activity);
        if (progressDialog == null) {
            progressDialog = new Dialog(activity, R.style.loading_dialog);
            progressDialog.setContentView(R.layout.loading_dialog);
            dialogLruCache.put(activity, progressDialog);
        }
        progressDialog.setCancelable(cancel);
        progressDialog.setCanceledOnTouchOutside(cancel);
        progressDialog.show();
    }

    public static void showSuccess(@StringRes int msg) {
        showMsg(UIUtils.getString(msg), true);
    }

    public static void showSuccess(String msg) {
        showMsg(msg, true);
    }

    public static void showError(int msg) {
        showMsg(UIUtils.getString(msg), false);
    }

    public static void showError(String msg) {
        showMsg(msg, false);
    }



    private static void showMsg(final String msg, final boolean isSuccess) {
        dismissLoading();
        if (msg.isEmpty()) {
            return;
        }
        // 做主线程检查
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        if (!isMainThread) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToast(msg, isSuccess);
                }
            });
        } else {
            showToast(msg, isSuccess);
        }
    }

    private static void showToast(String msg, boolean isSuccess) {
        UGToast.showToast(msg);
    }
}
