package com.jj.base.common.floatwindow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.jj.base.common.floatwindow.FloatPermission.Companion.hasPermission
import com.jj.base.common.floatwindow.FloatPermission.Companion.request
import org.jetbrains.anko.contentView

@Suppress("DEPRECATION")
@SuppressLint("ViewConstructor")
class FloatView(
    private val mContext: Context,
    private val mOutScreen: Boolean,
    private val requestPermission: Boolean,
    private val mPermissionListener: FloatPermissionListener?
) : FrameLayout(mContext), IFloatView {

    private val mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mLayoutParams = WindowManager.LayoutParams().apply {
        format = PixelFormat.TRANSLUCENT
        windowAnimations = 0
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                (if (mOutScreen)
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                else
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }

    private var mX = 0
    private var mY = 0
    private var isRemove = false
    private var listener: OnTouchListener? = null

    override fun setSize(width: Int, height: Int) {
        mLayoutParams.width = width
        mLayoutParams.height = height
    }

    override fun setView(view: View) {
        removeAllViews()
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        mLayoutParams.gravity = gravity
        mX = xOffset
        mLayoutParams.x = mX
        mY = yOffset
        mLayoutParams.y = mY
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (listener?.onTouch(this, ev) == true) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    override fun cancelLongPress() {
        cancelAllLongPress(this)
    }

    private fun cancelAllLongPress(view: ViewGroup) {
        val childCount = view.childCount
        for (i in 0 until childCount) {
            view.getChildAt(i).let {
                it.cancelLongPress()
                if (it is ViewGroup) {
                    cancelAllLongPress(it)
                }
            }
        }
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        listener = l
    }

    override fun init() {
        visibility = View.GONE
        isRemove = false;
        if (mContext is Activity) {
            //和Activity绑定时
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
            mLayoutParams.token = mContext.contentView?.windowToken
            try {
                mWindowManager.addView(this, mLayoutParams)
            } catch (e: Exception) {
                //失败时不做处理
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            req()
        } else {
            try {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                mWindowManager.addView(this, mLayoutParams)
            } catch (e: Exception) {
                try {
                    mWindowManager.removeView(this)
                } catch (e: Exception) {
                }
                req()
            }
        }
    }

    private fun req() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        if (requestPermission) {
            request(mContext, object :
                FloatPermissionListener {
                override fun onSuccess() {
                    mWindowManager.addView(this@FloatView, mLayoutParams)
                    mPermissionListener?.onSuccess()
                }

                override fun onFail() {
                    mPermissionListener?.onFail()
                }
            })
        } else if (hasPermission(mContext)) {
            mWindowManager.addView(this, mLayoutParams)
            mPermissionListener?.onSuccess()
        } else {
            mPermissionListener?.onFail()
        }
    }

    override fun destroy() {
        isRemove = true
        try {
            mWindowManager.removeView(this)
        } catch (e: Exception) {
        }
    }

    override fun updateXY(x: Int, y: Int) {
        if (isRemove) return
        mX = x
        mLayoutParams.x = mX
        mY = y
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(this, mLayoutParams)
    }

    override fun updateX(x: Int) {
        if (isRemove) return
        mX = x
        mLayoutParams.x = mX
        mWindowManager.updateViewLayout(this, mLayoutParams)
    }

    override fun updateY(y: Int) {
        if (isRemove) return
        mY = y
        mLayoutParams.y = mY
        mWindowManager.updateViewLayout(this, mLayoutParams)
    }

    override fun getPositionX(): Int {
        return mX
    }

    override fun getPositionY(): Int {
        return mY
    }


}