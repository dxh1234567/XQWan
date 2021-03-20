package com.jj.xqwan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.jj.xqwan.common.utils.Utils


/**
 *  Created By duXiaHui
 *  on 2021/2/5
 */
class DialectResultView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 骰子的结果的列表
     * */
    var resultArray: List<Int>? = null
        set(value) {
            field = value
            // 结果改变，重绘
            invalidate()
        }

    private var picHalfWidth = 0

    private val drawableHashMap = HashMap<String, Drawable>(6)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            picHalfWidth = width / 6
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (resultArray == null) {
            return
        }
        canvas.save()
        // 绘制第一行的前两个数字
        drawFirstLineNumber(canvas)
        // 绘制第二行的三个数字
        drawLastLineNumber(canvas)
    }

    private fun drawFirstLineNumber(canvas: Canvas) {
        canvas.translate(picHalfWidth.toFloat(), 0f)
        for (index in 0..1) {
            getIconByNumber(resultArray!![index].toString()).draw(canvas)
            canvas.translate((picHalfWidth * 2).toFloat(), 0f)
        }
    }

    private fun drawLastLineNumber(canvas: Canvas) {
        canvas.restore()
        canvas.save()
        canvas.translate(0f, (height / 2).toFloat())
        for (index in 2..4) {
            getIconByNumber(resultArray!![index].toString()).draw(canvas)
            canvas.translate((picHalfWidth * 2).toFloat(), 0f)
        }
    }

    /**
     * 记载指定id的图片
     * */
    private fun getIconByNumber(number: String): Drawable {
        return if (drawableHashMap[number] != null) {
            return drawableHashMap[number]!!
        } else {
            val drawable = Utils.getDrawable(context,
                context.resources.getIdentifier("icon_dialect_$number", "drawable", context.packageName)
            )
            drawable.bounds = Rect(0, 0, picHalfWidth * 2, picHalfWidth * 2)
            drawableHashMap[number] = drawable
            drawable
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 清空图片
        drawableHashMap.clear()
    }

}