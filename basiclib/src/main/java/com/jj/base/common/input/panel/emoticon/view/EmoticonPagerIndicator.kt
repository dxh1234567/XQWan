package com.jj.base.common.input.panel.emoticon.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.dip

internal class EmoticonPagerIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var itemCount = 0
    private var selectedWidth = context.dip(20)
    private var dia = context.dip(10)
    private var space = context.dip(5)
    private var lastPositionOffset = 0f
    private var firstVisiblePosition = 0
    private var indicatorColor = 0xffeeeeee
    private var rectf: RectF
    private var paint: Paint


    init {
        setWillNotDraw(false)
        rectf = RectF()
        paint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = indicatorColor.toInt()
            style = Paint.Style.FILL
        }
    }

    private var lastEmoticonSetIndex = 0

    fun setupWithViewPager(viewPager: EmoticonViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateWithPosition(viewPager, position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                val emoticonSetIndex = viewPager.getEmoticonSetIndex(position)
                if (emoticonSetIndex != lastEmoticonSetIndex) {
                    return
                }
                val indexInEmoticonSet = viewPager.getIndexInEmoticonSet(position)
                firstVisiblePosition = indexInEmoticonSet
                lastPositionOffset = positionOffset
                invalidate()
            }
        })
    }

    fun updateWithPosition(
        viewPager: EmoticonViewPager,
        position: Int
    ) {
        itemCount = viewPager.getPagerIndicatorCount(position)
        layoutParams.width = ((itemCount - 1) * (space + dia) + selectedWidth)
        layoutParams.height = dia
        val indexInEmoticonSet = viewPager.getIndexInEmoticonSet(position)
        firstVisiblePosition = indexInEmoticonSet
        lastPositionOffset = 0F
        lastEmoticonSetIndex = viewPager.getEmoticonSetIndex(position)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isInEditMode || itemCount == 0) {
            return
        }

        for (i in 0 until itemCount) {
            var left: Float
            var right: Float

            if (i < firstVisiblePosition) {
                left = i * (dia + space).toFloat()
                right = left + dia
            } else if (i == firstVisiblePosition) {
                left = i * (dia + space).toFloat()
                right = left + dia + (selectedWidth - dia) * (1 - lastPositionOffset)
            } else if (i == firstVisiblePosition + 1) {
                left =
                    (i - 1) * (space + dia) + dia + (selectedWidth - dia) * (1 - lastPositionOffset) + space
                right = i * (space + dia).toFloat() + selectedWidth
            } else {
                left = (i - 1) * (dia + space).toFloat() + (selectedWidth + space)
                right = (i - 1) * (dia + space).toFloat() + (selectedWidth + space) + dia
            }

            rectf.left = left
            rectf.top = 0f
            rectf.right = right
            rectf.bottom = dia.toFloat()

            canvas.drawRoundRect(rectf, dia.toFloat() / 2, dia.toFloat() / 2, paint)
        }

    }
}
