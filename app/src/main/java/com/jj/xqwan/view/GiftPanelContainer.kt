package com.jj.xqwan.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cn.jj.base.utils.ViewUtil


/**
 *  Created By duXiaHui
 *  on 2021/2/26
 */

class GiftPanelContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private var view : View ? = null

    fun setContainerViewPage(view : View ){
        this.view  = view
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        view?.let {
            ViewUtil.getDescendantRectRelativeToAncestor(it, this, tempRect)
            val res = tempRect.contains(ev.x.toInt(), ev.y.toInt())
            Log.e("---",tempRect.toString())
            Log.e("---",res.toString())
        }



        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val TAG = "LiveRoom.GiftPanelContainer"
        private val tempRect = Rect()
    }
}