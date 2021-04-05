package com.jj.base.common.input.panel.action.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jj.base.common.input.panel.action.BaseAction
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.dip

internal class ActionPagerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val actions = mutableListOf<BaseAction>()
    private val buttons = mutableListOf<View>()
    private val titles = mutableListOf<TextView>()

    private var minDivide: Int = 0
    private var defaultTextMarginTop: Int = 0

    private var listener: ActionPanel.OnActionResultListener? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        minDivide = context.dip(8)
        defaultTextMarginTop = context.dip(2)

        isClickable = true

        for (i in 1..8) {
            val button = Button(context).apply {
                layoutParams = MarginLayoutParams(
                        MarginLayoutParams.WRAP_CONTENT,
                        MarginLayoutParams.WRAP_CONTENT
                )
            }
            buttons.add(button)
            addView(button)

            val textView = TextView(context).apply {
                layoutParams = MarginLayoutParams(
                        MarginLayoutParams.WRAP_CONTENT,
                        MarginLayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                ellipsize = TextUtils.TruncateAt.END
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                includeFontPadding = false
            }
            titles.add(textView)
            addView(textView)
        }
    }

    fun setActions(list: List<BaseAction>) {
        actions.clear()
        if (list.size > 8) {
            actions.addAll(list.dropLast(list.size - 8))
        } else {
            actions.addAll(list)
        }
        actions.forEachWithIndex { i, baseAction ->
            buttons[i].setOnClickListener {
                baseAction.onClick {
                    listener?.onActionResult(it, baseAction)
                }
            }
        }
        requestLayout()
    }

    fun setOnActionResultListener(listener: ActionPanel.OnActionResultListener?) {
        this.listener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        var parentHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(titles[0], widthMeasureSpec, heightMeasureSpec)
        val textHeight = titles[0].measuredHeight

        var drawableWidth = 0
        var drawableHeight = 0
        actions.forEach {
            drawableWidth = Math.max(drawableWidth, it.getDrawable(context).intrinsicWidth)
            drawableHeight = Math.max(drawableHeight, it.getDrawable(context).intrinsicHeight)
        }

        var itemWidth = drawableWidth
        var itemHeight = drawableHeight + defaultTextMarginTop + textHeight

        if (widthMode != MeasureSpec.EXACTLY) {
            parentWidth = Math.min(parentWidth, itemWidth * 4 + minDivide * 5)
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            parentHeight = Math.min(parentHeight, itemHeight * 2 + minDivide * 3)
        }

        val drawableMaxWidth = (parentWidth - minDivide * 5) / 4
        val drawableMaxHeight = (parentHeight - minDivide * 3) / 2 - textHeight - defaultTextMarginTop

        if (drawableHeight > drawableMaxHeight || drawableWidth > drawableMaxWidth) {
            if (drawableHeight * drawableMaxWidth > drawableWidth * drawableMaxHeight) {
                drawableWidth = drawableWidth * drawableMaxHeight / drawableHeight
                drawableHeight = drawableMaxHeight
            } else {
                drawableHeight = drawableHeight * drawableMaxWidth / drawableWidth
                drawableWidth = drawableMaxWidth
            }
        }

        itemWidth = drawableWidth
        itemHeight = drawableHeight + defaultTextMarginTop + textHeight

        val divideWidth = (parentWidth - itemWidth * 4) / 5
        val divideHeight = (parentHeight - itemHeight * 2) / 3

        actions.forEachWithIndex { i, baseAction ->

            val x = if (i >= 4) i - 4 else i
            val y = i / 4

            baseAction.left = divideWidth + x * (divideWidth + itemWidth)
            baseAction.right = baseAction.left + itemWidth

            baseAction.top = divideHeight + y * (divideHeight + itemHeight)
            baseAction.bottom = baseAction.top + itemHeight

            baseAction.drawableHeight = drawableHeight
            baseAction.drawableWidth = drawableWidth
        }

        setMeasuredDimension(parentWidth, parentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        actions.forEachWithIndex { i, baseAction ->

            buttons[i].apply {
                layoutParams = layoutParams.apply {
                    height = baseAction.drawableHeight
                    width = baseAction.drawableWidth
                }
                setBackgroundResource(baseAction.icon)
                layout(
                        baseAction.left,
                        baseAction.top,
                        baseAction.right,
                        baseAction.top + baseAction.drawableHeight
                )
            }

            titles[i].apply {
                text = baseAction.title
                layoutParams = layoutParams.apply {
                    width = baseAction.drawableWidth
                }
                layout(
                        baseAction.left,
                        baseAction.top + baseAction.drawableHeight + defaultTextMarginTop,
                        baseAction.right,
                        baseAction.bottom
                )
            }

        }
    }
}
