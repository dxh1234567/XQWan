package com.jj.xqwan.activity.ui

import android.app.Dialog
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.jj.xqwan.R


/**
 *  Created By duXiaHui
 *  on 2021/4/3
 */
open class BaseDialog(context: Context) : Dialog(context, R.style.Dialog) {

    /**
     * 设置Dialog的宽高
     * */
    protected fun setWidthAndHeight(width: Int, height: Int) {
        val wl = window?.attributes
        // 以下这两句是为了保证按钮可以水平满屏
        wl?.width = width
        wl?.height = height
        onWindowAttributesChanged(wl)
    }


}