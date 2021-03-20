package cn.jj.base.common.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import cn.jj.basiclib.R
import cn.jj.base.utils.ScreenUtil
import java.util.*

class LineWaveView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mLineColor: Int = 0
    private var mLineNum: Int = DEFAULT_LINE_NUM

    private lateinit var mPaint: Paint
    private var mAnimator: ValueAnimator? = null
    private val mLineRectFs = ArrayList<RectF>()
    private lateinit var mOriginLineHeights: Array<Float>
    private var mAnimatorValue = 0f
    private val mRandom = Random()
    private var mComputed = false

    init {
        init(attrs, defStyleAttr, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.LineWaveView, defStyleAttr, defStyleRes
        )
        mLineColor = a.getColor(R.styleable.LineWaveView_lv_line_color, Color.BLACK)
        mLineNum = a.getInt(R.styleable.LineWaveView_lv_line_num, DEFAULT_LINE_NUM)
        a.recycle()
        if (mLineNum < 1) {
            throw IllegalArgumentException("LineWaveView: line num must large or equal 1")
        }
        mPaint = Paint()
        mPaint.color = mLineColor
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureDimension(DEFAULT_WIDTH, widthMeasureSpec)
        val height = measureDimension(DEFAULT_HEIGHT, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    @SuppressLint("SwitchIntDef")
    private fun measureDimension(defaultSize: Int, measureSpec: Int) =
            when (MeasureSpec.getMode(measureSpec)) {
                MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
                MeasureSpec.AT_MOST -> Math.min(defaultSize, MeasureSpec.getSize(measureSpec)) //wrap_content 设置最大值
                else -> defaultSize
            }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calc()
        if (mComputed) {
            drawWaves(canvas)
        }
    }

    private fun drawWaves(canvas: Canvas) {
        canvas.save()
        canvas.translate(0f, height.toFloat())
        mLineRectFs.forEach {
            canvas.drawRoundRect(it, ROUND_RECT_CORNER, ROUND_RECT_CORNER, mPaint)
        }
        canvas.restore()
    }

    private fun calc() {
        if (mComputed || width == 0 || height == 0 ||
                paddingTop >= height || paddingBottom >= height ||
                (paddingTop + paddingBottom) >= height ||
                paddingLeft >= width || paddingRight >= width ||
                (paddingLeft + paddingRight) >= width
        ) {
            return
        }
        mComputed = true
        val lineMaxHeight = height - paddingTop - paddingBottom
        if (mLineRectFs.isEmpty()) {
            val lineWidth = (width - paddingLeft - paddingRight) * 1.0f /
                    ((mLineNum + 1) * LINE_SPACE_RATIO + mLineNum)
            mOriginLineHeights = Array(mLineNum) { 0f }
            mLineRectFs.apply {
                for (i in 0 until mLineNum) {
                    val left = ((i + 1) * LINE_SPACE_RATIO + i) * lineWidth + paddingLeft
                    if (DEFAULT_LINE_NUM == mLineNum) {
                        mOriginLineHeights[i] = DEFAULT_LINE_HEIGHTS[i] * lineMaxHeight
                    } else {
                        mOriginLineHeights[i] = mRandom.nextFloat() * lineMaxHeight
                    }
                    add(RectF(left,
                            -(mOriginLineHeights[i] + paddingBottom),
                            left + lineWidth,
                            -paddingBottom.toFloat())
                    )
                }
            }
        }

        for (i in 0 until mLineNum) {
            val rectF = mLineRectFs[i]
            val quotient = (mOriginLineHeights[i] + lineMaxHeight * mAnimatorValue) / lineMaxHeight
            val remainder = (mOriginLineHeights[i] + lineMaxHeight * mAnimatorValue) % lineMaxHeight
            when (quotient.toInt()) {
                0, 2 -> {
                    rectF.top = -(remainder + paddingBottom)
                }
                1 -> {
                    rectF.top = -(height - paddingTop - remainder)
                }
            }
        }
    }

    override fun setVisibility(v: Int) {
        if (visibility != v) {
            super.setVisibility(v)
            if (v == View.GONE || v == View.INVISIBLE) {
                stopAnim()
            } else {
                startAnim()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnim()
    }

    private fun startAnim() {
        mAnimator?.cancel()
        mAnimator = ValueAnimator.ofFloat(0f, 1f, 2f).apply {
            addUpdateListener { anim ->
                mAnimatorValue = anim.animatedValue as Float
                mComputed = false
                invalidate()
            }
            interpolator = LinearInterpolator()
            duration = ANIM_DURATION
            repeatCount = INFINITE
        }
        mAnimator?.start()
    }

    private fun stopAnim() {
        mAnimator?.cancel()
        mAnimator = null
    }

    companion object {
        const val DEFAULT_LINE_NUM = 4
        const val ANIM_DURATION = 1200L
        const val ROUND_RECT_CORNER = 30f
        const val LINE_SPACE_RATIO = 2f //Line间距与自身宽度比例
        val DEFAULT_WIDTH = ScreenUtil.dp2px(30f)
        val DEFAULT_HEIGHT = DEFAULT_WIDTH * 5 / 7
        val DEFAULT_LINE_HEIGHTS = FloatArray(DEFAULT_LINE_NUM).apply {
            this[0] = 0.5f
            this[1] = 1f
            this[2] = 0.3f
            this[3] = 0.6f
        }
    }
}
