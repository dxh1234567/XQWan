package cn.jj.base.common.floatwindow

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi

interface FloatPermissionListener {
    fun onSuccess() {}
    fun onFail() {}
}

class FloatPermission : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAlertWindowPermission()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 100000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100000) {
            if (hasPermissionOnActivityResult(this)) {
                for (listener in mPermissionListenerList) {
                    listener.onSuccess()
                }
                mPermissionListenerList.clear()
            } else {
                for (listener in mPermissionListenerList) {
                    listener.onFail()
                }
                mPermissionListenerList.clear()
            }
        }
        finish()
    }

    companion object {
        private val mPermissionListenerList = mutableListOf<FloatPermissionListener>()

        @JvmStatic
        @Synchronized
        fun request(context: Context, permissionListener: FloatPermissionListener) {
            if (hasPermission(context)) {
                permissionListener.onSuccess()
                return
            }
            mPermissionListenerList.add(permissionListener)
            val intent = Intent(context, FloatPermission::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                mPermissionListenerList.remove(permissionListener)
                permissionListener.onFail()
            }
        }

        @JvmStatic
        fun hasPermission(context: Context): Boolean {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    Settings.canDrawOverlays(context)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    hasPermissionBelowMarshmallow(context)
                }
                else -> {
                    true
                }
            }
        }

        fun hasPermissionOnActivityResult(context: Context): Boolean {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                return hasPermissionForO(context)
            }
            return hasPermission(context)
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun hasPermissionBelowMarshmallow(context: Context): Boolean {
            return try {
                val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val dispatchMethod = AppOpsManager::class.java.getMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
                AppOpsManager.MODE_ALLOWED == dispatchMethod.invoke(
                    manager,
                    24,
                    Binder.getCallingUid(),
                    context.applicationContext.packageName
                ) as Int
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 用于判断8.0时是否有权限，仅用于OnActivityResult
         * 针对8.0官方bug:在用户授予权限后Settings.canDrawOverlays或checkOp方法判断仍然返回false
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun hasPermissionForO(context: Context): Boolean {
            try {
                val mgr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val viewToAdd = View(context)
                val params = WindowManager.LayoutParams(0, 0).apply {
                    type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    format = PixelFormat.TRANSPARENT
                }
                viewToAdd.layoutParams = params
                mgr.addView(viewToAdd, params)
                mgr.removeView(viewToAdd)
                return true
            } catch (e: Exception) {
                Log.e("hasPermissionForO e:", e.toString())
            }
            return false
        }
    }
}