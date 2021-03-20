package com.jj.xqwan.view

import com.jj.xqwan.R



import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.jj.xqwan.common.utils.Utils
import java.util.*

/**
 * Created by li.zhipeng on 2018/6/22.
 *
 *      大转盘View
 */
class TurnTableView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    companion object {

        /**
         * 指针偏移转盘的距离
         * */
        const val INDICATOR_OFFSET = 18

        /**
         * 转盘的边距
         * */
        const val PADDING = 60

        /**
         * 转盘的标准宽度
         * */
        const val STANDARD_WIDTH = 368

        /**
         * 绘制文字距离中心的偏移值
         * */
        const val TEXT_OFFSET = -10f

        /**
         * 文字行间距
         * */
        const val LINE_SPACING = 10
    }

    /**
     * 背景缩放的比例
     * */
    private var scale: Float = 1f

    /**
     * 画笔
     * */
    private val paint by lazy {
        val paint = Paint()
        paint.isDither = true
        paint.isAntiAlias = true
        paint
    }

    private var rectF: RectF = RectF()

    /**
     * 绘制转盘的大小
     * */
    private var tableSize = 0f

    /**
     * 绘制转盘的中心点
     * */
    private var centerX = 0f

    private var centerY = 0f

    /**
     * 要旋转的角度, 用户旋转动画
     * */
    private var rotateAngle: Float = 0f

    /**
     * 是否已经停止， 绘制选中的选项
     * */
    private var isStop = false

    /**
     * 选项的文字
     * */
    var giftItemText = arrayOf("猜拳1", "猜拳2", "猜拳3", "猜拳4", "猜拳5", "猜拳6", "猜拳\n猜拳7", "猜拳\n猜拳8")
        set(value) {
            field = value
            invalidate()
        }
    /**
     * 绘制文字距离中心的偏移值
     * */
    private var textOffset = Utils.dip2px(context, TEXT_OFFSET)

    /**
     * 转盘的结果
     * */
    private var resultPosition: Int = 0

    /**
     * 动画
     * */
    private var animator: ValueAnimator? = null

    /**
     * 开始按钮的区域
     * */
    private var startButtonRectF = RectF()

    private val drawableManager = TurnTableDrawableManager()

    private val gestureDetector = TurnTableGesture()

    /**
     * 顶部指针的偏移值
     *
     */
    private var indicatorOffsetScale = 0f

    var textSize: Float = Utils.dip2px(context, 11f)

    var listener: OnTurnTableResultListener? = null

    var onTurnTableAnimEndListener: OnTurnTableAnimEndListener? = null

    /**
     * 是否允许点击,滑动停止的时候获取这个状态
     * */
    var canPlay: Boolean = true
        set(value) {
            field = value
            // 如果当前没有旋转，需要重绘
            if (!isAnimating()) {
                canPlayTemp = value
                invalidate()
            }
        }
    /**
     * 备份的是否允许点击
     * */
    var canPlayTemp: Boolean = true

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            // 计算缩放比例
            scale = width.toFloat() / STANDARD_WIDTH

            indicatorOffsetScale = INDICATOR_OFFSET * scale
            centerX = width.toFloat() / 2
            centerY = (height.toFloat() / 2) + indicatorOffsetScale
            // 计算转盘的大小
            // 如果宽大于高 或者 宽大于高的差，小于指针的偏移距离
            tableSize = if (width >= height - indicatorOffsetScale) {
                (height.toFloat() - indicatorOffsetScale) / 2
            }
            // 如果高大于宽
            else {
                (width.toFloat() - indicatorOffsetScale) / 2
            }

            val padding = scale * PADDING
            rectF.set(width / 2 - tableSize + padding,
                height / 2 - tableSize + padding + indicatorOffsetScale,
                centerX + tableSize - padding,
                centerY + tableSize - padding)
            // 开始按钮的点击区域
            startButtonRectF.set(
                centerX - drawableManager.getStartDrawable().bounds.width() / 2,
                centerY - drawableManager.getStartDrawable().bounds.height() / 2,
                centerX + drawableManager.getStartDrawable().bounds.width() / 2,
                centerY + drawableManager.getStartDrawable().bounds.height() / 2)

        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        // 画出背景
        drawBg(canvas)

        // 绘制所有奖项的圆弧
        drawTurnTableItem(canvas)

        // 绘制开始按钮
        drawStartButton(canvas)

        // 绘制指针
        drawTurnTableIndicator(canvas)
    }

    /**
     * 绘制背景
     * */
    private fun drawBg(canvas: Canvas) {
        canvas.translate((width - tableSize * 2) / 2, indicatorOffsetScale)
        drawableManager.getBgDrawable().draw(canvas)
    }

    /**
     * 绘制每一个奖项的圆弧
     * */
    private fun drawTurnTableItem(canvas: Canvas) {
        canvas.restore()
        canvas.save()
        // 画出普通状态的背景
        val scalePadding = PADDING * scale
        val translateX = (width.toFloat() - tableSize * 2 + scalePadding) / 2
        val translateY = indicatorOffsetScale + scalePadding / 2
        canvas.translate(translateX, translateY)
        // 加上旋转的角度，实现动画
        paint.color = Color.BLACK
        canvas.rotate(rotateAngle, centerX - translateX, tableSize - scalePadding / 2)
        drawableManager.getContentNormalDrawable().draw(canvas)

        // 如果停止绘制高亮
        if (isStop) {
            // 绘制文字
            canvas.restore()
            canvas.save()
            // 加上选中的位置的角度
            canvas.translate(((width - drawableManager.getContentSelectedDrawable().bounds.width()) / 2).toFloat(), translateY)
            drawableManager.getContentSelectedDrawable().draw(canvas)
            // 绘制闪烁动画
            drawableManager.getSelectAnimDrawable().draw(canvas)
        }
        // 绘制文字
        canvas.restore()
        canvas.save()
        canvas.drawCircle(centerX, indicatorOffsetScale + tableSize, 5f, paint)

        canvas.translate(centerX, indicatorOffsetScale + tableSize)



        paint.color = Color.WHITE
        paint.textSize = textSize
        // 加上选装的角度，实现动画
        canvas.rotate(rotateAngle - 90)
        for (i in 0 until giftItemText.size) {
            // 绘制背景
            drawText(canvas, giftItemText[i], tableSize / 2, 0f, paint, 90f)
            canvas.rotate(45f)
        }
    }

    /**
     * 绘制开始按钮
     * */
    private fun drawStartButton(canvas: Canvas) {
        canvas.restore()
        canvas.save()
        // 如果是按下或者是不允许点击，显示按下的灰色按钮
        val drawable = if (isPressed || !canPlayTemp) {
            drawableManager.getStartPressedDrawable()
        } else {
            drawableManager.getStartDrawable()
        }
        canvas.translate(centerX - drawable.bounds.width() / 2,
            indicatorOffsetScale + tableSize - drawable.bounds.height() / 2)
        drawable.draw(canvas)
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, angle: Float) {
        if (angle != 0f) {
            canvas.rotate(angle, x, y)
        }
        val textArray = text.split("\n")
        // 绘制文字
        textArray.forEachIndexed { index, item ->
            val textWidth = paint.measureText(item)
            val textHeight = index * (paint.textSize + LINE_SPACING)
            canvas.drawText(item, x - textWidth / 2, textOffset + textHeight, paint)
        }
        if (angle != 0f) {
            canvas.rotate(-angle, x, y)
        }
    }

    /**
     * 绘制指针
     * */
    private fun drawTurnTableIndicator(canvas: Canvas) {
        canvas.restore()
        canvas.save()
        canvas.translate(centerX - drawableManager.getIndicatorDrawable().bounds.width() / 2,
            0f)
        drawableManager.getIndicatorDrawable().draw(canvas)
    }

    /**
     * 开始转动
     *
     * @param position 结果的位置
     * @param callback 是否回调结果函数监听
     * */
    fun start(position: Int, callback: Boolean = true) {
        // 如果正在旋转，直接返回
        if (isAnimating()) {
            return
        }
        startAnimation(getTotalRotateAngle(position, callback))
    }

    /**
     * 开始转动
     * */
    private fun startAnimation(angle: Float) {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, angle)
            animator!!.interpolator = AccelerateDecelerateInterpolator()
            animator!!.duration = 3000
            animator!!.addUpdateListener {
                rotateAngle = it.animatedValue as Float
                if (it.animatedFraction == 1.0f) {
                    isStop = true
                    // 动画停止，获取新的状态
                    canPlayTemp = canPlay
                    onTurnTableAnimEndListener?.onTurnTableAnimEnd(canPlayTemp)
                }
                invalidate()
            }
        } else {
            animator!!.setFloatValues(rotateAngle % 360, angle)
        }
        isStop = false
        animator!!.start()
    }

    private fun getTotalRotateAngle(position: Int, callback: Boolean): Float {
        val lastPos = resultPosition
        // 计算得到的结果
        this.resultPosition = position
        if (callback) {
            listener?.onTurnTableResult(resultPosition)
        }
        return rotateAngle % 360 + 1080f + (lastPos - resultPosition) * 45
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 是否正在显示动画
     * */
    private fun isAnimating(): Boolean = animator?.isRunning ?: false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 重绘一次，如果是停止状态，显示选中的动画
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
        if (animator != null) {
            animator!!.end()
            animator = null
        }
    }

    /**
     * 手势处理
     * */
    private inner class TurnTableGesture {

        /**
         * 是否点击在开始按钮
         * */
        private var tapInStartButton: Boolean = false

        fun onTouchEvent(event: MotionEvent): Boolean {
            if (!isAnimating()) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> onDown(event)
                    MotionEvent.ACTION_UP -> onUp(event)
                    MotionEvent.ACTION_CANCEL -> onCancel()
                }
            }
            return true
        }

        fun onDown(e: MotionEvent) {
            if (!canPlayTemp) {
                return
            }
            tapInStartButton = startButtonRectF.contains(e.x, e.y)
            if (tapInStartButton) {
                isPressed = true
                invalidate()
            }
        }

        fun onUp(e: MotionEvent) {
            if (!canPlayTemp) {
                return
            }
            if (tapInStartButton) {
                if (startButtonRectF.contains(e.x, e.y)) {
                    isPressed = false
                    start(Random().nextInt(8))
                } else {
                    isPressed = false
                    invalidate()
                }
            }
        }

        fun onCancel() {
            if (!canPlayTemp) {
                return
            }
            if (isPressed) {
                isPressed = false
                invalidate()
            }
        }

    }

    inner class TurnTableDrawableManager {

        /**
         * 转盘的背景图片
         * */
        private var bgDrawable: Drawable? = null

        @Synchronized
        fun getBgDrawable(): Drawable {
            if (bgDrawable == null) {
                bgDrawable = Utils.getDrawable(context, R.drawable.turn_table_bg)
                // 把图片缩放到指定的大小
                bgDrawable!!.bounds = Rect(0, 0, (tableSize * 2).toInt(), (tableSize * 2).toInt())
            }
            return bgDrawable!!
        }

        /**
         * 转盘的开始按钮
         * */
        private var startDrawable: Drawable? = null

        @Synchronized
        fun getStartDrawable(): Drawable {
            if (startDrawable == null) {
                startDrawable = Utils.getDrawable(context, R.drawable.turn_table_start)
                // 把图片缩放到指定的大小
                startDrawable!!.bounds = Rect(0, 0,
                    (132 * scale).toInt(),
                    (132 * scale).toInt())
            }
            return startDrawable!!
        }

        /**
         * 转盘的开始按钮按下图片
         * */
        private var startPressedDrawable: Drawable? = null

        @Synchronized
        fun getStartPressedDrawable(): Drawable {
            if (startPressedDrawable == null) {
                startPressedDrawable = Utils.getDrawable(context, R.drawable.turn_table_start_pressed)
                // 把图片缩放到指定的大小
                startPressedDrawable!!.bounds = Rect(0, 0,
                    (132 * scale).toInt(),
                    (132 * scale).toInt())
            }
            return startPressedDrawable!!
        }

        /**
         * 指针的图片
         * */
        private var indicatorDrawable: Drawable? = null

        @Synchronized
        fun getIndicatorDrawable(): Drawable {
            if (indicatorDrawable == null) {
                indicatorDrawable = Utils.getDrawable(context, R.drawable.turn_table_indicator)
                // 把图片缩放到指定的大小
                indicatorDrawable!!.bounds = Rect(0, 0,
                    (58 * scale).toInt(),
                    (88 * scale).toInt())
            }
            return indicatorDrawable!!
        }

        /**
         * 转盘普通的样式
         * */
        private var contentNormalDrawable: Drawable? = null

        @Synchronized
        fun getContentNormalDrawable(): Drawable {
            if (contentNormalDrawable == null) {
                contentNormalDrawable = Utils.getDrawable(context, R.drawable.turn_table_normal)
                // 把图片缩放到指定的大小
                contentNormalDrawable!!.bounds = Rect(0, 0,
                    (tableSize * 2 - PADDING * scale).toInt(),
                    (tableSize * 2 - PADDING * scale).toInt())
            }
            return contentNormalDrawable!!
        }

        /**
         * 转盘选中的样式
         * */
        private var contentSelectedDrawable: Drawable? = null

        @Synchronized
        fun getContentSelectedDrawable(): Drawable {
            if (contentSelectedDrawable == null) {
                contentSelectedDrawable = Utils.getDrawable(context, R.drawable.turn_table_selected)
                // 把图片缩放到指定的大小
                contentSelectedDrawable!!.bounds = Rect(0, 0,
                    (120 * scale).toInt(),
                    (154 * scale).toInt())
            }
            return contentSelectedDrawable!!
        }

        /**
         * 转盘选中的高亮动画图
         * */
        private var animStatus1: Drawable? = null

        /**
         * 转盘选中的高亮动画图
         * */
        private var animStatus2: Drawable? = null

        /**
         * 转盘选中的高亮动画图
         * */
        private var currentStatus: Drawable? = null

        @Synchronized
        fun getSelectAnimDrawable(): Drawable {
            if (animStatus1 == null) {
                animStatus1 = Utils.getDrawable(context, R.drawable.flash_anim0)
                // 把图片缩放到指定的大小
                animStatus1!!.bounds = Rect(0, 0,
                    (120 * scale).toInt(),
                    (154 * scale).toInt())
            }
            if (animStatus2 == null) {
                animStatus2 = Utils.getDrawable(context, R.drawable.flash_anim1)
                // 把图片缩放到指定的大小
                animStatus2!!.bounds = Rect(0, 0,
                    (120 * scale).toInt(),
                    (154 * scale).toInt())
            }

            currentStatus = when (currentStatus) {
                null -> animStatus1
                animStatus1 -> animStatus2
                else -> animStatus1
            }

            // 自动重绘
            handler.postDelayed({
                if (isStop) {
                    invalidate()
                }

            }, 250)

            return currentStatus!!
        }
    }

    interface OnTurnTableResultListener {
        fun onTurnTableResult(position: Int)
    }

    interface OnTurnTableAnimEndListener {
        fun onTurnTableAnimEnd(canPlay: Boolean)
    }

}