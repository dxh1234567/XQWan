package com.jj.xqwan.activity.ui.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.jj.xqwan.R
import com.jj.xqwan.activity.ui.BaseDialog


/**
 *  Created By duXiaHui
 *  on 2021/4/3
 */

class GuideDynamicListDialog(context: Context) : BaseDialog(context) {

    private var guideImg: ImageView

    private var time = 0

    init {
        window?.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_guide_moment_list)
        guideImg = findViewById(R.id.guide_img)
//        setCanceledOnTouchOutside(false)
//        setCancelable(false)
        guideImg.setOnClickListener {
            if (time == 0) {
                guideImg.setImageResource(R.drawable.guide_dynamic_invite)
                time++
            } else {
//                dismiss()
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val outOfBounds = isOutOfBounds(context, event)

        Log.e("---",outOfBounds.toString())
        return super.onTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏
        val dm = context.resources.displayMetrics
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, (dm.heightPixels*0.4).toInt())
    }


    open fun isOutOfBounds(
        context: Context,
        event: MotionEvent
    ): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val slop = ViewConfiguration.get(context).scaledWindowTouchSlop
        val decorView = window.decorView
        Log.e("-000--",slop.toString())
        Log.e("-000x--",x.toString())
        Log.e("-000y--",y.toString())
        Log.e("-1000x--", decorView.width.toString())
        Log.e("-1000y--", decorView.height.toString())

        return (x < -slop || y < -slop || x > decorView.width + slop
                || y > decorView.height + slop)
    }
}