package com.jj.base.common.input.panel.action

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable

abstract class BaseAction(
    val icon: Int,
    val title: String
) {

    private var drawable: Drawable? = null

    /*测量参数*/
    var left = 0
    var right = 0
    var top = 0
    var bottom = 0
    var drawableWidth = 0
    var drawableHeight = 0
    /*测量参数*/

    abstract fun onClick(callback: (message: String) -> Unit)

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun getDrawable(context: Context): Drawable {
        return drawable ?: context.resources.getDrawable(icon).apply {
            drawable = this
        }
    }
}
