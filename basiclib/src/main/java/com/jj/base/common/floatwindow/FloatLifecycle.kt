package com.jj.base.common.floatwindow

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.jj.base.utils.ForegroundCallbacks

/**
 * 悬浮窗显示条件管理
 */
interface FloatLifecycleListener {
    fun onShowByFilter()
    fun onHideByFilter()
    fun onBackToDesktop()
    fun onBackToApp()
}

/**
 * 悬浮窗显示条件管理
 */
class FloatLifecycle internal constructor(
    private val applicationContext: Context,
    private val filterFlag: Boolean,
    private val activities: Array<out Class<*>>?,
    private val mLifecycleListener: FloatLifecycleListener
) : BroadcastReceiver(), ActivityLifecycleCallbacks, ForegroundCallbacks.Listener {

    companion object {
        private const val SYSTEM_DIALOG_REASON_KEY = "reason"
        private const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
    }

    fun init() {
        (applicationContext as Application).registerActivityLifecycleCallbacks(this)
        applicationContext.registerReceiver(this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        ForegroundCallbacks.get().addListener(this)
    }

    fun destroy() {
        (applicationContext as Application).unregisterActivityLifecycleCallbacks(this)
        applicationContext.unregisterReceiver(this)
        ForegroundCallbacks.get().removeListener(this)
    }

    private fun needShow(activity: Activity): Boolean {
        if (activities == null) {
            return true
        }
        for (a in activities) {
            if (a.isInstance(activity)) {
                return filterFlag
            }
        }
        return !filterFlag
    }

    override fun onActivityResumed(activity: Activity) {
        if (activities != null) {
            if (needShow(activity)) {
                mLifecycleListener.onShowByFilter()
            } else {
                mLifecycleListener.onHideByFilter()
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null && action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason =
                intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
            if (SYSTEM_DIALOG_REASON_HOME_KEY == reason) {
                mLifecycleListener.onBackToDesktop()
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onBecameForeground() {
        mLifecycleListener.onBackToApp()
    }

    override fun onBecameBackground() {
        mLifecycleListener.onBackToDesktop()
    }

}