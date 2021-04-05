package com.jj.base.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.jj.basiclib.R
import org.jetbrains.anko.dip
import java.util.*

class CodeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr), CodeAction, TextWatcher {

    private var mFigures: Int = 0//需要输入的位数
    private var mVerCodeMargin: Int = 0//验证码之间的间距
    private var mBottomSelectedColor: Int = 0//底部选中的颜色
    private var mBottomNormalColor: Int = 0//未选中的颜色
    private var mBottomLineHeight: Float = 0.toFloat()//底线的高度
    private var mSelectedBackgroundColor: Int = 0//选中的背景颜色
    private var mCursorWidth: Int = 0//光标宽度
    private var mCursorColor: Int = 0//光标颜色
    private var mCursorDuration: Int = 0//光标闪烁间隔

    private var onCodeChangedListener: CodeAction.OnCodeChangedListener? = null
    private var mCurrentPosition = 0
    private var mEachRectLength = 0//每个矩形的边长

    private lateinit var mSelectedBackgroundPaint: Paint
    private lateinit var mNormalBackgroundPaint: Paint
    private lateinit var mBottomSelectedPaint: Paint
    private lateinit var mBottomNormalPaint: Paint
    private lateinit var mCursorPaint: Paint

    // 控制光标闪烁
    private var isCursorShowing: Boolean = false
    private lateinit var mCursorTimerTask: TimerTask
    private lateinit var mCursorTimer: Timer

    private var isPassword = false

    init {
        initAttrs(attrs)
        //防止出现下划线
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        initPaint()
        initCursorTimer()
        isFocusableInTouchMode = true
        addTextChangedListener(this)
        isPassword = checkIsPassword()
    }

    /**
     * 初始化paint
     */
    private fun initPaint() {
        mSelectedBackgroundPaint = Paint()
        mSelectedBackgroundPaint.color = mSelectedBackgroundColor
        mNormalBackgroundPaint = Paint()
        mNormalBackgroundPaint.color = getColor(android.R.color.transparent)

        mBottomSelectedPaint = Paint()
        mBottomNormalPaint = Paint()
        mBottomSelectedPaint.color = mBottomSelectedColor
        mBottomNormalPaint.color = mBottomNormalColor
        mBottomSelectedPaint.strokeWidth = mBottomLineHeight
        mBottomNormalPaint.strokeWidth = mBottomLineHeight

        mCursorPaint = Paint()
        mCursorPaint.isAntiAlias = true
        mCursorPaint.color = mCursorColor
        mCursorPaint.style = Paint.Style.FILL_AND_STROKE
        mCursorPaint.strokeWidth = mCursorWidth.toFloat()
    }

    /**
     * 初始化Attrs
     */
    private fun initAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText)
        mFigures = ta.getInteger(R.styleable.CodeEditText_figures, 4)
        mVerCodeMargin = ta.getDimension(R.styleable.CodeEditText_verCodeMargin, 0f).toInt()
        mBottomSelectedColor = ta.getColor(
            R.styleable.CodeEditText_bottomLineSelectedColor,
            currentTextColor
        )
        mBottomNormalColor = ta.getColor(
            R.styleable.CodeEditText_bottomLineNormalColor,
            getColor(android.R.color.darker_gray)
        )
        mBottomLineHeight = ta.getDimension(
            R.styleable.CodeEditText_bottomLineHeight,
            context.dip(5).toFloat()
        )
        mSelectedBackgroundColor = ta.getColor(
            R.styleable.CodeEditText_selectedBackgroundColor,
            getColor(android.R.color.darker_gray)
        )
        mCursorWidth =
            ta.getDimension(R.styleable.CodeEditText_cursorWidth, context.dip(1).toFloat()).toInt()
        mCursorColor =
            ta.getColor(R.styleable.CodeEditText_cursorColor, getColor(android.R.color.darker_gray))
        mCursorDuration =
            ta.getInteger(
                R.styleable.CodeEditText_cursorDuration,
                DEFAULT_CURSOR_DURATION
            )
        ta.recycle()

        layoutDirection = View.LAYOUT_DIRECTION_LTR
    }

    private fun initCursorTimer() {
        mCursorTimerTask = object : TimerTask() {
            override fun run() {
                // 通过光标间歇性显示实现闪烁效果
                isCursorShowing = !isCursorShowing
                postInvalidate()
            }
        }
        mCursorTimer = Timer()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 启动定时任务，定时刷新实现光标闪烁
        mCursorTimer.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorDuration.toLong())
    }

    override fun onDetachedFromWindow() {
        mCursorTimer.cancel()
        super.onDetachedFromWindow()
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        isPassword = checkIsPassword()
    }

    override fun setCursorVisible(visible: Boolean) {
        super.setCursorVisible(visible)//隐藏光标的显示
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthResult: Int
        val heightResult: Int
        //最终的宽度
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        widthResult = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            context.resources.displayMetrics.widthPixels
        }
        //每个矩形形的宽度
        mEachRectLength = (widthResult - mVerCodeMargin * (mFigures - 1)) / mFigures
        //最终的高度
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        heightResult = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mEachRectLength
        }
        setMeasuredDimension(widthResult, heightResult)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            requestFocus()
            setSelection(text!!.length)
            showKeyBoard(context)
            return false
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        mCurrentPosition = text!!.length
        val width = mEachRectLength - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom
        for (i in 0 until mFigures) {
            canvas.save()
            val start = width * i + i * mVerCodeMargin
            val end = width + start
            //画一个矩形
            if (i == mCurrentPosition) {//选中的下一个状态
                canvas.drawRect(
                    start.toFloat(),
                    0f,
                    end.toFloat(),
                    height.toFloat(),
                    mSelectedBackgroundPaint
                )
            } else {
                canvas.drawRect(
                    start.toFloat(),
                    0f,
                    end.toFloat(),
                    height.toFloat(),
                    mNormalBackgroundPaint
                )
            }
            canvas.restore()
        }
        //绘制文字
        val value = text!!.toString()
        for (i in value.indices) {
            canvas.save()
            val start = width * i + i * mVerCodeMargin
            val x = (start + width / 2).toFloat()
            val paint = paint
            paint.textAlign = Paint.Align.CENTER
            paint.color = currentTextColor
            val fontMetrics = paint.fontMetrics
            val baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(
                if (isPassword)
                    "\u2022"
                else
                    value[i].toString(),
                x, baseline, paint
            )
            canvas.restore()
        }
        //绘制底线
        for (i in 0 until mFigures) {
            canvas.save()
            val lineY = height - mBottomLineHeight / 2
            val start = width * i + i * mVerCodeMargin
            val end = width + start
            if (i < mCurrentPosition) {
                canvas.drawLine(
                    start.toFloat(),
                    lineY,
                    end.toFloat(),
                    lineY,
                    mBottomSelectedPaint
                )
            } else {
                canvas.drawLine(start.toFloat(), lineY, end.toFloat(), lineY, mBottomNormalPaint)
            }
            canvas.restore()
        }
        //绘制光标
        if (!isCursorShowing && isCursorVisible && mCurrentPosition < mFigures && hasFocus()) {
            canvas.save()
            val startX = mCurrentPosition * (width + mVerCodeMargin) + width / 2
            val startY = height / 4
            val endY = height - height / 4
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                startX.toFloat(),
                endY.toFloat(),
                mCursorPaint
            )
            canvas.restore()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        mCurrentPosition = text!!.length
        postInvalidate()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        mCurrentPosition = text!!.length
        postInvalidate()
        if (onCodeChangedListener != null) {
            onCodeChangedListener!!.onVerCodeChanged(text!!, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        mCurrentPosition = text!!.length
        postInvalidate()
        if (text!!.length == mFigures) {
            if (onCodeChangedListener != null) {
                onCodeChangedListener!!.onInputCompleted(text!!)
            }
        } else if (text!!.length > mFigures) {
            text!!.delete(mFigures, text!!.length)
        }
    }

    override fun setFigures(figures: Int) {
        mFigures = figures
        postInvalidate()
    }

    override fun setCodeMargin(margin: Int) {
        mVerCodeMargin = margin
        postInvalidate()
    }

    override fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int) {
        mBottomSelectedColor = getColor(bottomSelectedColor)
        postInvalidate()
    }

    override fun setBottomNormalColor(@ColorRes bottomNormalColor: Int) {
        mBottomSelectedColor = getColor(bottomNormalColor)
        postInvalidate()
    }

    override fun setSelectedBackgroundColor(@ColorRes selectedBackground: Int) {
        mSelectedBackgroundColor = getColor(selectedBackground)
        postInvalidate()
    }

    override fun setBottomLineHeight(bottomLineHeight: Int) {
        this.mBottomLineHeight = bottomLineHeight.toFloat()
        postInvalidate()
    }

    override fun setOnCodeChangedListener(listener: CodeAction.OnCodeChangedListener) {
        this.onCodeChangedListener = listener
    }

    private fun checkIsPassword(): Boolean {
        val variation = inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        val passwordInputType =
            variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        val webPasswordInputType =
            variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
        val numberPasswordInputType =
            variation == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        return passwordInputType || webPasswordInputType || numberPasswordInputType
    }

    /**
     * 返回颜色
     */
    private fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    fun showKeyBoard(context: Context) {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    companion object {

        private const val DEFAULT_CURSOR_DURATION = 400
    }
}

interface CodeAction {
    /**
     * 设置位数
     */
    fun setFigures(figures: Int)

    /**
     * 设置验证码之间的间距
     */
    fun setCodeMargin(margin: Int)

    /**
     * 设置底部选中状态的颜色
     */
    fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int)

    /**
     * 设置底部未选中状态的颜色
     */
    fun setBottomNormalColor(@ColorRes bottomNormalColor: Int)

    /**
     * 设置选择的背景色
     */
    fun setSelectedBackgroundColor(@ColorRes selectedBackground: Int)

    /**
     * 设置底线的高度
     */
    fun setBottomLineHeight(bottomLineHeight: Int)

    /**
     * 设置当验证码变化时候的监听器
     */
    fun setOnCodeChangedListener(listener: OnCodeChangedListener)

    /**
     * 验证码变化时候的监听事件
     */
    interface OnCodeChangedListener {

        /**
         * 当验证码变化的时候
         */
        fun onVerCodeChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        /**
         * 输入完毕后的回调
         */
        fun onInputCompleted(s: CharSequence) {}
    }
}