package com.jj.base.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;


/**
 * 状态栏导航栏控制  针对Activity设置
 * <p>
 * 目前的设计图：
 * 显示状态栏，但是布局要延伸到状态栏下
 * 不显示导航栏，上滑显示导航栏，自动隐藏，布局稳定
 * 背景多白色，需要注意状态栏和导航栏的内容颜色
 * <p>
 * 目前系统栏隐藏的实现方案：
 * 21(5.0)以上：状态栏透明，布局延伸到状态栏下，注意设置padding防止遮挡内容。导航栏透明，上滑显示，一秒后隐藏。
 * 19(4.4)以上：状态栏半透明，布局延伸到状态栏下，注意设置padding防止遮挡内容。导航栏半透明，上滑显示，一秒后隐藏。
 * <p>
 * 目前系统栏背景色/图标色的实现方案
 * 23(6.0)以上：可以设置亮状态栏，即状态栏背景色亮色时，字体颜色黑色
 */

public class SystemBarsUtil {

    private static final int SET_DECOR_VIEW_FLAG = 1;
    private static final String TAG = "SystemBarsUtil";

    private int flagL =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private int flagLFull =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private int flagK =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private int flagKFull =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /**
     * 需要设置的Activity
     */
    private WeakReference<Window> windowReference;

    /**
     * 是否是亮状态栏，仅针对23以上
     */
    private boolean lightStatusBar = false;

    private boolean hideStatusBar = true;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_DECOR_VIEW_FLAG:
                    setDecorViewFlag();
                    break;
            }
        }
    };

    public SystemBarsUtil(Window window) {
        this.windowReference = new WeakReference<>(window);
    }

    public void setStatusBarFullTransparent() {

        Window window = windowReference.get();
        if (window == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }

        View decorView = window.getDecorView();
        if (Constant.ATLEAST_LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            setDecorViewFlag();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    mHandler.removeMessages(SET_DECOR_VIEW_FLAG);
                    mHandler.sendEmptyMessageDelayed(SET_DECOR_VIEW_FLAG, 1000);
                }
            });
        } else if (Constant.ATLEAST_KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setDecorViewFlag();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    mHandler.removeMessages(SET_DECOR_VIEW_FLAG);
                    mHandler.sendEmptyMessageDelayed(SET_DECOR_VIEW_FLAG, 1000);
                }
            });
        }
    }

    private void setDecorViewFlag() {
        Window window = windowReference.get();
        if (window == null) {
            return;
        }

        View decorView = window.getDecorView();
        if (Constant.ATLEAST_MARSHMALLOW) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagLFull);
            } else {
                decorView.setSystemUiVisibility(lightStatusBar ? flagL | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : flagL);
            }
        } else if (Constant.ATLEAST_LOLLIPOP) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagLFull);
            } else {
                decorView.setSystemUiVisibility(flagL);
            }
        } else if (Constant.ATLEAST_KITKAT) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagKFull);
            } else {
                decorView.setSystemUiVisibility(flagK);
            }
        } else {
            decorView.setSystemUiVisibility(View.GONE);
        }
    }


    /**
     * View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0x00002000 = 0010 0000 0000 0000
     */
    public void setLightStatusBar(boolean light) {
        lightStatusBar = light;
        setDecorViewFlag();
    }

    public void setStatusBarHiddenInThisView(boolean hidden) {
        hideStatusBar = hidden;
        setDecorViewFlag();
    }

    public void setStatusBarHiddenInThisWindow(boolean hidden) {
        Window window = windowReference.get();
        if (window == null) {
            return;
        }

        if (hidden) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
