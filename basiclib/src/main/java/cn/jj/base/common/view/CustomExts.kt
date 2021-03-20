package cn.jj.base.common.view

/**
 * 自定义的拓展方法
 */

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import cn.jj.base.utils.Constant

/**
 * 刘海适配
 */
fun Activity.handleDisplayCutoutMode() {
    if (Constant.ATLEAST_P) {
        window.decorView.let { view ->
            if (ViewCompat.isAttachedToWindow(view)) {
                realHandleDisplayCutoutMode(window, view)
            } else {
                view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewDetachedFromWindow(v: View?) {
                    }

                    override fun onViewAttachedToWindow(v: View?) {
                        view.removeOnAttachStateChangeListener(this)
                        realHandleDisplayCutoutMode(window, view)
                    }
                })
            }
        }
    }
}

@TargetApi(28)
fun realHandleDisplayCutoutMode(window: Window, decorView: View) {
    if (decorView.rootWindowInsets != null && decorView.rootWindowInsets.displayCutout != null) {
        val params = window.attributes
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.attributes = params
    }
}

/**
 * 全屏+全透明的状态栏
 */
fun Activity.setStatusBarFullTransparent() {
    if (Constant.ATLEAST_LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    } else if (Constant.ATLEAST_KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

fun Dialog.setStatusBarFullTransparent() {
    if (Constant.ATLEAST_LOLLIPOP) {
        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    } else if (Constant.ATLEAST_KITKAT) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

fun Dialog.setNavigationBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         window?.navigationBarColor = Color.TRANSPARENT
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}

/**
 * 设置亮状态栏
 */
fun Activity.setLightStatusBar(boolean: Boolean) {
    if (Constant.ATLEAST_MARSHMALLOW) {
        val systemUiVisibility = window.decorView.systemUiVisibility

        //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0x00002000 = 0010 0000 0000 0000
        val isLight = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR ==
                (systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        if (isLight != boolean) {
            window.decorView.systemUiVisibility =
                systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun View.addStatusBarPadding() {
    if (Constant.ATLEAST_KITKAT) {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val statusBarHeight = resources.getDimensionPixelSize(resourceId)
            setPadding(paddingLeft, paddingTop + statusBarHeight, paddingRight, paddingBottom)
        }
    }
}

fun View.fitViewLoc() {
    if (Constant.ATLEAST_KITKAT) {
        fitsSystemWindows = true
        //重新进行FitSystemWindows的适配
        ViewCompat.requestApplyInsets(this)
    }
}