package com.jj.xqwan.view

import com.jj.xqwan.R


import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.jj.base.common.glide.Corners
import kotlin.math.min

/**
 * 通过裁剪的方式，实现圆角矩形图，适用于大图加载，可避免一张图因圆角等转换导致生成多张图片
 * */
private val sTempRect = RectF()
private val sTempCornerRadiusArray = FloatArray(8)

class AdvanceImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isCircle = false
    private var cornerRadiusPercent = 0f
    private var cornerRadius = 0f
    private var corners = Corners.CORNER_ALL
    private var bgStroke = 0f
    private var bgDrawable: Drawable? = null
    private var fgDrawable: Drawable? = null
    private var clipPath: Path? = null

    private var calculated = false

    init {
        initAttr(attrs)

    }

    private fun initAttr(attrs: AttributeSet? = null) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.AdvanceImageView
        )
        isCircle = a.getBoolean(R.styleable.AdvanceImageView_AI_isCircle, false)
        cornerRadiusPercent = a.getFloat(R.styleable.AdvanceImageView_AI_cornerRadiusPercent, 0f)
        cornerRadius =
            a.getDimensionPixelSize(R.styleable.AdvanceImageView_AI_cornerRadius, 0).toFloat()
        corners = a.getInt(R.styleable.AdvanceImageView_AI_corners, Corners.CORNER_ALL)
        bgStroke = a.getDimensionPixelSize(R.styleable.AdvanceImageView_AI_bgStroke, 0).toFloat()
        bgDrawable = a.getDrawable(R.styleable.AdvanceImageView_AI_bgDrawable)
        fgDrawable = a.getDrawable(R.styleable.AdvanceImageView_AI_fgDrawable)
        a.recycle()
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
        calculated = false
    }

    fun setCornerRadiusPercent(cornerRadiusPercent: Float) {
        this.cornerRadiusPercent = cornerRadiusPercent
        calculated = false
    }

    fun setFgDrawable(fgDrawable: Drawable?) {
        this.fgDrawable = fgDrawable
        calculated = false
    }

    fun setBgDrawable(bgDrawable: Drawable?) {
        this.bgDrawable = bgDrawable
        calculated = false
    }

    fun setIsCricle(isCircle: Boolean) {
        this.isCircle = isCircle
        calculated = false
    }

    fun setBgStroke(bgStroke: Float) {
        this.bgStroke = bgStroke
        calculated = false
    }

    fun setRoundCorners(corners: Int) {
        this.corners = corners
        calculated = false
    }

    private fun calc() {
        if (calculated) {
            return
        }
        calculated = true
        calcClipPath()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val layerType =
                if (clipPath != null) View.LAYER_TYPE_SOFTWARE else View.LAYER_TYPE_HARDWARE
            if (layerType != getLayerType()) {
                setLayerType(layerType, null)
            }
        }
    }

    private fun calcClipPath() {
        if (isCircle) {
            clipPath = Path().apply {
                val w = width - paddingLeft - paddingRight
                val h = height - paddingTop - paddingBottom
                val radius = min(w, h) / 2f - bgStroke
                addCircle(width / 2f, height / 2f, radius, Path.Direction.CW)
            }
            return
        }
        val radius = if (cornerRadius > 0f)
            cornerRadius
        else if (cornerRadiusPercent > 0f)
            min(width, height) * cornerRadiusPercent * 0.5f
        else 0f
        if (radius > 0f) {
            clipPath = Path().apply {
                sTempRect.set(
                    bgStroke + paddingLeft,
                    bgStroke + paddingTop,
                    width.toFloat() - bgStroke - paddingRight,
                    height.toFloat() - bgStroke - paddingBottom
                )
                if (corners == Corners.CORNER_ALL) {
                    addRoundRect(sTempRect, radius, radius, Path.Direction.CW)
                    return@apply
                }
                for (i in 0 until 8) {
                    sTempCornerRadiusArray[0] = 0f
                }
                if ((corners and Corners.CORNER_TOP_LEFT) != 0) {
                    sTempCornerRadiusArray[0] = radius
                    sTempCornerRadiusArray[1] = radius
                }
                if ((corners and Corners.CORNER_TOP_RIGHT) != 0) {
                    sTempCornerRadiusArray[2] = radius
                    sTempCornerRadiusArray[3] = radius
                }
                if ((corners and Corners.CORNER_BOTTOM_LEFT) != 0) {
                    sTempCornerRadiusArray[4] = radius
                    sTempCornerRadiusArray[5] = radius
                }
                if ((corners and Corners.CORNER_BOTTOM_RIGHT) != 0) {
                    sTempCornerRadiusArray[6] = radius
                    sTempCornerRadiusArray[7] = radius
                }
                addRoundRect(sTempRect, sTempCornerRadiusArray, Path.Direction.CW)
            }
            return
        }
        clipPath = null
    }

    override fun onDraw(canvas: Canvas) {
        calc()
        bgDrawable?.apply {
            setBounds(0, 0, width, height)
            draw(canvas)
        }
        clipPath?.let {
            canvas.clipPath(it)
        }
        super.onDraw(canvas)
        fgDrawable?.apply {
            setBounds(0, 0, width, height)
            draw(canvas)
        }
    }
}