package com.jj.base.common.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.Gravity.*
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import com.jj.base.common.glide.*
import com.jj.base.common.input.panel.emoticon.data.EmoticonManager
import com.jj.base.utils.BitmapUtil
import com.jj.base.utils.LogUtil
import com.jj.base.utils.ScreenUtil
import com.jj.base.utils.Utility.NO_CACHE
import com.jj.base.utils.Utility.NO_STORE
import com.jj.base.utils.getController
import com.jj.basiclib.R
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.lang.ref.WeakReference

private const val TAG = "CommonBindingAdapter"

@BindingAdapter(
    value = ["image",
        "isCircle",
        "placeholder",
        "error",
        "memoryPolicy",
        "bgImage",
        "fgImage",
        "bgStroke",
        "fgStroke",
        "cornerRadiusPercent",
        "cornerRadius",
        "corners"],
    requireAll = false
)
fun <T> bindImageFromUrl(
    view: ImageView,
    image: T?,
    isCircle: Boolean = false,
    placeholder: Drawable? = null,
    error: Drawable? = null,
    memoryPolicy: Int = 0,
    bgImage: Drawable? = null,
    fgImage: Drawable? = null,
    bgStroke: Int = 0,
    fgStroke: Int = 0,
    cornerRadiusPercent: Float = 0f,
    cornerRadius: Float = 0f,
    corners: Int = Corners.CORNER_ALL
) {
    //传入参数错误时，尝试填充error
    if (image == null || (image is String && image.isEmpty()) || image == 0) {
        error?.let {
            view.setImageDrawable(error)
        }
        return
    }

    //传入Drawable，直接填充
    if (image is Drawable) {
        view.setImageDrawable(image)
        return
    }

    //传入int，切没有特殊需求时，直接填充
    if (image is Int && !isCircle && cornerRadiusPercent == 0f && bgImage == null && fgImage == null) {
        view.setImageResource(image)
        return
    }

    //使用Glide加载，注意设置好imageview大小
    GlideApp.with(view)
        .load(
            when {
                image is String && TextUtils.isDigitsOnly(image) ->
                    Integer.parseInt(image as String)
                image is Int -> image
                image is File -> image
                image is Uri -> image
                image is String -> image
                else -> {
                    throw IllegalArgumentException("错误的image类型")
                }
            }
        )
        .error(error)
        .placeholder(placeholder)
        .skipMemoryCache(1 == (memoryPolicy and NO_CACHE))
        .diskCacheStrategy(
            if (1 == (memoryPolicy and NO_STORE)) {
                DiskCacheStrategy.NONE
            } else
                DiskCacheStrategy.ALL
        )
        .apply {
            val list = mutableListOf<Transformation<Bitmap>>()
                .apply {
                    when (view.scaleType) {
                        ImageView.ScaleType.CENTER_CROP ->
                            add(CenterCrop())
                        ImageView.ScaleType.CENTER_INSIDE,
                        ImageView.ScaleType.FIT_XY ->
                            add(CenterInside())
                        ImageView.ScaleType.FIT_CENTER,
                        ImageView.ScaleType.FIT_START,
                        ImageView.ScaleType.FIT_END ->
                            add(FitCenter())
                        else -> {
                            //do nothing
                        }
                    }
                    if (fgImage != null) add(LayerTransformation(fgImage, fgStroke, true))
                    if (isCircle) add(CircleCrop())
                    if (cornerRadiusPercent > 0) {
                        add(PercentRoundedCorners(cornerRadiusPercent, corners))
                    } else if (cornerRadius > 0) {
                        add(CustomRoundedCorners(cornerRadius, corners))
                    }
                    if (bgImage != null) add(LayerTransformation(bgImage, bgStroke, false))
                }
            if (list.size > 0) {
                transforms(*list.toTypedArray())
            }
        }
        .transition(DrawableTransitionOptions.withCrossFade())
        .listener(object : RequestListener<Drawable> {
            var viewRef = WeakReference<View>(view)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                viewRef.get()?.setTag(R.id.image_view_src_target, image)
                return false
            }
        })
//        .transition(DrawableTransitionOptions.withCrossFade(300))
        .into(view)

}


/**
 * EpoxyRecyclerView 使用setController绑定TypedEpoxyController时的通用方法
 *
 * 通过反射获取TypedEpoxyController
 *
 */
@Suppress("UNCHECKED_CAST")
@BindingAdapter("epItems")
fun <T> setEpoxyRecyclerViewItems(recyclerView: EpoxyRecyclerView, data: T) {
    try {
        val controller =
            recyclerView.getController() as TypedEpoxyController<T>
        controller.setData(data)
    } catch (e: ClassCastException) {
        throw RuntimeException("EpoxyRecyclerView使用items传入的数据类型，与其TypedEpoxyController不一致")
    }
}

/**
 * EpoxyRecyclerView 使用setController绑定TypedEpoxyController时的通用方法
 *
 * 通过反射获取TypedEpoxyController
 *
 */
@Suppress("UNCHECKED_CAST")
@BindingAdapter("epPagedItems")
fun <T> setEpoxyRecyclerViewPagedItems(recyclerView: EpoxyRecyclerView, data: PagedList<T>?) {
    try {
        val controller =
            recyclerView.getController() as PagedListEpoxyController<T>
        controller.submitList(data)
    } catch (e: ClassCastException) {
        throw RuntimeException("EpoxyRecyclerView使用items传入的数据类型，与其PagedListEpoxyController不一致")
    }
}

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    val visibility = if (show) View.VISIBLE else View.GONE
    if (view.visibility != visibility) {
        view.visibility = visibility
    }
}

@BindingAdapter("visible")
fun visible(view: View, show: Boolean) {
    val visibility = if (show) View.VISIBLE else View.INVISIBLE
    if (view.visibility != visibility) {
        view.visibility = visibility
    }
}

@BindingAdapter(
    value = ["bgImage",
        "tintColor",
        "cornerRadiusPercent",
        "cornerRadius",
        "corners"],
    requireAll = false
)
fun <T> bindBgImage(
    view: View,
    image: T?,
    @ColorInt tintColor: Int? = null,
    cornerRadiusPercent: Float = 0f,
    cornerRadius: Float = 0f,
    corners: Int = Corners.CORNER_ALL
) {
    if (image == null || (image is String && image.isEmpty()) || image == 0) {
        return
    }
    if (image is Drawable && cornerRadiusPercent == 0f && cornerRadius == 0f) {
        view.background =
            if (tintColor != null) {
                BitmapUtil.getTintDrawable(image, tintColor, null)
            } else {
                image
            }
        return
    }
    //使用Glide加载
    GlideApp.with(view)
        .load(
            when {
                image is String && TextUtils.isDigitsOnly(image) -> Integer.parseInt(image as String) as Any
                image is Int -> image as Any
                image is File -> image as Any
                image is Uri -> image as Any
                image is String -> image as Any
                image is Drawable -> image
                else -> {
                    throw IllegalArgumentException("错误的image类型")
                }
            }
        )
        .apply {
            val list = mutableListOf<Transformation<Bitmap>>()
                .apply {
                    if (cornerRadiusPercent > 0) {
                        add(PercentRoundedCorners(cornerRadiusPercent, corners))
                    } else if (cornerRadius > 0) {
                        add(CustomRoundedCorners(cornerRadius, corners))
                    }
                }
            if (list.size > 0) {
                transforms(*list.toTypedArray())
            }
        }
        .into(BackgroundTarget(view, tintColor))
}

@BindingAdapter("app:layout_constraintHorizontal_bias")
fun bindConstraintLayout(view: View, bias: Float) {
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.setHorizontalBias(view.id, bias)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintStart_toEndOf")
fun bindConstraintLayoutStartToEnd(view: View, id: Int) {
    if (id == -1) {
        return
    }
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.connect(view.id, 6, id, 7)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintEnd_toStartOf")
fun bindConstraintLayoutEndToStart(view: View, id: Int) {
    if (id == -1) {
        return
    }
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.connect(view.id, 7, id, 6)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintGuide_percent")
fun bindConstraintLayout(view: Guideline, percent: Float) {
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.setGuidelinePercent(view.id, percent)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintDimensionRatio")
fun bindConstraintLayoutDimensionRatio(view: View, ratio: String) {
    if (view.getTag(R.id.view_constraintDimensionRatio) == ratio) {
        return
    }
    view.setTag(R.id.view_constraintDimensionRatio, ratio)
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.setDimensionRatio(view.id, ratio)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintHeight_max")
fun bindConstraintLayoutMax(view: View, max: String) {
    if (view.getTag(R.id.view_constraintHeight_max) == max) {
        return
    }
    view.setTag(R.id.view_constraintHeight_max, max)
    val values = max.split(",")
    val maxHeight = if (values[0] == "w") {
        ScreenUtil.getScreenWidth() * values[1].toFloat()
    } else if (values[0] == "h") {
        ScreenUtil.getScreenHeight() * values[1].toFloat()
    } else {
        throw java.lang.IllegalArgumentException("错误的格式")
    }
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.constrainMaxHeight(view.id, maxHeight.toInt())
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintWidth_percent")
fun bindConstraintLayoutWidthPercent(view: View, percent: Float) {
    if (view.getTag(R.id.view_constraintWidthPercent) == percent) {
        return
    }
    if (view.id == View.NO_ID) {
        view.id = View.generateViewId()
    }
    view.setTag(R.id.view_constraintWidthPercent, percent)
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.constrainPercentWidth(view.id, percent)
        cs.applyTo(parent)
    }
}

@BindingAdapter("app:layout_constraintHeight_percent")
fun bindConstraintLayoutHeightPercent(view: View, percent: Float) {
    if (view.getTag(R.id.view_constraintHeightPercent) == percent) {
        return
    }
    if (view.id == View.NO_ID) {
        view.id = View.generateViewId()
    }
    view.setTag(R.id.view_constraintHeightPercent, percent)
    val parent = view.parent
    if (parent is ConstraintLayout) {
        val cs = ConstraintSet()
        cs.clone(parent)
        cs.constrainPercentHeight(view.id, percent)
        cs.applyTo(parent)
    }
}


@BindingAdapter("selectState")
fun bindSelectState(view: View, selected: Boolean) {
    view.isSelected = selected
}

@BindingAdapter("setLinkMovent")
fun setLinkMovent(view: TextView, enable: Boolean?) {
    enable?.let {
        if (it) {
            view.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}


/**
 * TextView Drawable支持设置Gravity,目前只支持设置单个方向的Drawable
 */
@BindingAdapter(
    "leftDrawable",
    "topDrawable",
    "rightDrawable",
    "bottomDrawable",
    "leftDrawableGravity",
    "topDrawableGravity",
    "rightDrawableGravity",
    "bottomDrawableGravity",
    requireAll = false
)
fun setCompoundDrawables(
    view: TextView, left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?,
    leftGravity: Int, topGravity: Int, rightGravity: Int, bottomGravity: Int
) =
    view.postDelayed({
        val width = view.width
        val height = view.height
        val lineHeight = view.lineHeight
        left?.let {
            setTextViewDrawableBound(
                width,
                height,
                lineHeight,
                it,
                0,
                if (NO_GRAVITY == leftGravity) CENTER_VERTICAL else leftGravity
            )
        }
        top?.let {
            setTextViewDrawableBound(
                width,
                height,
                lineHeight,
                it,
                1,
                if (NO_GRAVITY == topGravity) CENTER_HORIZONTAL else topGravity
            )
        }
        right?.let {
            setTextViewDrawableBound(
                width,
                height,
                lineHeight,
                it,
                2,
                if (NO_GRAVITY == rightGravity) CENTER_VERTICAL else rightGravity
            )
        }
        bottom?.let {
            setTextViewDrawableBound(
                width,
                height,
                lineHeight,
                it,
                3,
                if (NO_GRAVITY == bottomGravity) CENTER_HORIZONTAL else bottomGravity
            )
        }
        view.setCompoundDrawables(left, top, right, bottom)
    }, 0)

private fun setTextViewDrawableBound(
    width: Int, height: Int, lineHeight: Int,
    drawable: Drawable, drawableType: Int, drawableGravity: Int
) {
    LogUtil.d(
        TAG,
        "setTextViewDrawableBound() called with: width = $width, height = $height, lineHeight = $lineHeight, drawable = $drawable, drawableType = $drawableType, drawableGravity = $drawableGravity"
    )
    val drawableWidth = drawable.intrinsicWidth
    val drawableHeight = drawable.intrinsicHeight
    var left = 0
    var top = 0
    when (drawableType) {
        //Drawable LEFT OR RIGHT
        0, 2 -> {
            top = when (drawableGravity) {
                TOP -> -(height - lineHeight) shr 1 //TOP
                BOTTOM -> (height - lineHeight) shr 1 //BOTTOM
                else -> 0 //CENTER_VERTICAL
            }
        }
        //Drawable TOP OR BOTTOM
        1, 3 -> {
            left = when (drawableGravity) {
                LEFT, START -> -(width - drawableWidth) shr 1  //LEFT
                RIGHT, END -> (width - drawableWidth) shr 1 //RIGHT
                else -> 0 //CENTER_HORIZONTAL
            }
        }
    }
    drawable.setBounds(left, top, left + drawableWidth, top + drawableHeight)
}

@BindingAdapter("textWithEmoticon")
fun setTextWithEmoticon(textView: TextView, text: String) {
    EmoticonManager.filterEmoticon(textView, text)
}

@BindingAdapter("textWithEmoticon")
fun setTextWithEmoticon(textView: TextView, text: SpannableString) {
    EmoticonManager.filterEmoticon(textView, text)
}

@BindingAdapter("textWithEmoticon")
fun setTextWithEmoticon(textView: TextView, text: SpannableStringBuilder) {
    EmoticonManager.filterEmoticon(textView, text)
}

@BindingAdapter("onMultiClick")
fun onMultiClick(view: View, f: View.OnClickListener?) {
    f?.apply {
        view.setOnClickListener(object : OnMultiClickListener() {
            override fun onMultiClick(v: View) {
                f.onClick(v)
            }
        })
    }
}

@BindingAdapter("drawableLeft", "drawableWidth", "drawableHeight", requireAll = false)
fun setDrawableLeft(
    view: TextView,
    drawable: Drawable?,
    width: Int = 0,
    height: Int = 0
) {
    drawable?.apply {
        setBounds(
            0,
            0,
            if (width <= 0) intrinsicWidth else width,
            if (height <= 0) intrinsicHeight else height
        )
    }
    val drawables = view.compoundDrawables
    view.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3])
}

@BindingAdapter(
    "marginLeft",
    "marginTop",
    "marginRight",
    "marginBottom",
    "marginConfig",
    requireAll = false
)
fun setMargin(
    view: View,
    marginLeft: Float,
    marginTop: Float,
    marginRight: Float,
    marginBottom: Float,
    marginConfig: Int // Gravity#TOP BOTTOM LEFT RIGHT
) {
    if (marginConfig == 0) {
        return
    }
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        if (leftMargin != marginLeft.toInt()
            || topMargin != marginTop.toInt()
            || rightMargin != marginRight.toInt()
            || bottomMargin != marginBottom.toInt()
        ) {
            setMargins(
                marginConfig.let {
                    if (it and LEFT == LEFT)
                        marginLeft.toInt()
                    else leftMargin
                },
                marginConfig.let {
                    if (it and TOP == TOP)
                        marginTop.toInt()
                    else topMargin
                },
                marginConfig.let {
                    if (it and RIGHT == RIGHT)
                        marginRight.toInt()
                    else rightMargin
                },
                marginConfig.let {
                    if (it and BOTTOM == BOTTOM)
                        marginBottom.toInt()
                    else bottomMargin
                }
            )
        }
    }
}