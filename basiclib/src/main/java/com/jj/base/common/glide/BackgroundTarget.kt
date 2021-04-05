package com.jj.base.common.glide

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.jj.base.utils.BitmapUtil

class BackgroundTarget(view: View, private val tintColor: Int?) :
        CustomViewTarget<View, Drawable>(view) {

    private fun setDrawable(drawable: Drawable?) {
        view.background =
                if (tintColor != null) {
                    BitmapUtil.getTintDrawable(drawable, tintColor, null)
                } else {
                    drawable
                }
    }


    override fun onLoadFailed(errorDrawable: Drawable?) {
        setDrawable(errorDrawable)
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        setDrawable(placeholder)
    }

    override fun onResourceLoading(placeholder: Drawable?) {
        setDrawable(placeholder)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        setDrawable(resource)
    }

}
