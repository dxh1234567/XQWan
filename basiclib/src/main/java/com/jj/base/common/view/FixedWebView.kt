package com.jj.base.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView
import androidx.annotation.RequiresApi


/**
 * androidx.appcompat:appcompat:1.1.0 在部分Android5.x上存在兼容问题
 */
open class FixedWebView : WebView {
    constructor(context: Context) : super(getFixedContext(context)) {}

    constructor(context: Context, attrs: AttributeSet?) : super(getFixedContext(context), attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        getFixedContext(
            context
        ), attrs, defStyleAttr
    ) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        getFixedContext(context),
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }

    private val radiusArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private var needClip = false

    fun setRadius(
        leftTop: Float = 0f,
        rightTop: Float = 0f,
        rightBottom: Float = 0f,
        leftBottom: Float = 0f
    ) {
        needClip = true
        radiusArray[0] = leftTop
        radiusArray[1] = leftTop
        radiusArray[2] = rightTop
        radiusArray[3] = rightTop
        radiusArray[4] = rightBottom
        radiusArray[5] = rightBottom
        radiusArray[6] = leftBottom
        radiusArray[7] = leftBottom
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (needClip) {
            val path = Path()
            path.addRoundRect(
                RectF().apply {
                    set(
                        scrollX.toFloat(),
                        scrollY.toFloat(),
                        scrollX.toFloat() + width.toFloat(),
                        scrollY.toFloat() + height.toFloat()
                    )
                },
                radiusArray,
                Path.Direction.CW
            )
            canvas?.clipPath(path)
        }
        super.onDraw(canvas)
    }

    companion object {

        fun getFixedContext(context: Context): Context {
            return if (Build.VERSION.SDK_INT in 21..22) context.createConfigurationContext(
                Configuration()
            ) else context
        }
    }
}
