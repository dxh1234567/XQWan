package com.jj.base.common.view.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jj.base.common.view.WrapContentRecyclerView
import org.jetbrains.anko.dip

@SuppressLint("WrongConstant")
class ListPopup(
    context: Context,
    width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    orientation: Int = LinearLayoutManager.VERTICAL,
    maxHeight: Int = Integer.MAX_VALUE shr 2,
    maxWidth: Int = Integer.MAX_VALUE shr 2,
    resId: Int
) :
    NormalPopup<ListPopup>(
        context,
        width,
        height
    ) {

    var recyclerView: WrapContentRecyclerView

    init {
        this.animStyle(ANIM_GROW_FROM_CENTER)
            .preferredDirection(DIRECTION_BOTTOM)
            .bgColor(Color.WHITE)
            .arrow(true)
            .arrowSize(context.dip(15), context.dip(15))
            .arrowBorderWidth(2)
            .arrowBorderColor(Color.GRAY)
            .edgeProtection(context.dip(13))
            .view(WrapContentRecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context, orientation, false)
                setMaxHeight(maxHeight)
                setMaxWidth(maxWidth)
                setBackgroundResource(resId)
                clipToPadding = true
                recyclerView = this
            })
    }

}