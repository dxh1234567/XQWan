package cn.jj.base.common.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import cn.jj.base.utils.LogUtil

private const val TAG = "ClipImageView"

private const val NONE = 0
private const val DRAG = 1
private const val ZOOM = 2

class ClipImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    var clipWidth: Int = 0
        set(value) {
            field = value
            computed = false
        }
    var clipHeight: Int = 0
        set(value) {
            field = value
            computed = false
        }

    private val tempMatrix = Matrix()
    private val savedMatrix = Matrix()
    private val clipPaint = Paint()

    private var mode = NONE

    private val start = PointF()

    private val centerPoint = PointF()
    private var oldDist = 1f

    private val clipRect = RectF()
    private val strokeWidth = 5
    private var computed: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        LogUtil.i(TAG, "onTouchEvent： event:$event")
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(tempMatrix)
                start.set(event.x, event.y)
                mode = DRAG
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                savedMatrix.set(tempMatrix)
                mode = ZOOM
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            MotionEvent.ACTION_MOVE -> {

                if (mode == DRAG) {
                    tempMatrix.set(savedMatrix)
                    tempMatrix.postTranslate(event.x - start.x, event.y - start.y)
                    var p = getLeftPointF()
                    if (p.x > clipRect.left) {
                        tempMatrix.postTranslate(-p.x + clipRect.left, 0f)
                    }
                    p = getRightPointF()
                    if (p.x < clipRect.right) {
                        tempMatrix.postTranslate(clipRect.right - p.x, 0f)
                    }
                    p = getLeftPointF()
                    if (p.y > clipRect.top) {
                        tempMatrix.postTranslate(0f, -p.y + clipRect.top)
                    }
                    p = getRightPointF()
                    if (p.y < clipRect.bottom) {
                        tempMatrix.postTranslate(0f, clipRect.bottom - p.y)
                    }

                } else if (mode == ZOOM) {
                    val newDist = spacing(event)
                    tempMatrix.set(savedMatrix)
                    var scale = newDist / oldDist
                    if (scale < 1) {
                        scale += (1 - scale) / 2
                    }
                    // tempMatrix.postScale(scale, scale, centerPoint.x, centerPoint.y);
                    val p1 = getLeftPointF()
                    val p2 = getRightPointF()
                    val newLX = centerPoint.x - (centerPoint.x - p1.x) * scale
                    val newLY = centerPoint.y - (centerPoint.y - p1.y) * scale
                    val newRX = centerPoint.x - (centerPoint.x - p2.x) * scale
                    val newRY = centerPoint.y - (centerPoint.y - p2.y) * scale

                    if (newLX < clipRect.left &&
                            newLY < clipRect.top &&
                            newRX > clipRect.right &&
                            newRY > clipRect.bottom) {
                        tempMatrix.postScale(scale, scale, centerPoint.x, centerPoint.y)
                    } else {
                        val leftOffset = clipRect.left - newLX
                        val rightOffset = newRX - clipRect.right
                        val topOffset = clipRect.top - newLY
                        val bottomOffset = newRY - clipRect.bottom
                        if (leftOffset < 0
                                && Math.min(leftOffset, rightOffset) == leftOffset
                                && Math.min(leftOffset, topOffset) == leftOffset
                                && Math.min(leftOffset, bottomOffset) == leftOffset) {
                            tempMatrix.postScale((centerPoint.x - clipRect.left) / (centerPoint.x - p1.x),
                                    (centerPoint.x - clipRect.left) / (centerPoint.x - p1.x), centerPoint.x,
                                    centerPoint.y)
                        }
                        if (rightOffset < 0
                                && Math.min(leftOffset, rightOffset) == rightOffset
                                && Math.min(rightOffset, topOffset) == rightOffset
                                && Math.min(rightOffset, bottomOffset) == rightOffset) {
                            tempMatrix.postScale((centerPoint.x - clipRect.right) / (centerPoint.x - p2.x),
                                    (centerPoint.x - clipRect.right) / (centerPoint.x - p2.x), centerPoint.x,
                                    centerPoint.y)
                        }
                        if (topOffset < 0
                                && Math.min(topOffset, rightOffset) == topOffset
                                && Math.min(leftOffset, topOffset) == topOffset
                                && Math.min(topOffset, bottomOffset) == topOffset) {
                            tempMatrix.postScale(
                                    (centerPoint.y - clipRect.top) / (centerPoint.y - p1.y),
                                    (((centerPoint.y - clipRect.top)) / (centerPoint.y - p1.y)), centerPoint.x, centerPoint.y)
                        }
                        if ((bottomOffset < 0
                                        && Math.min(bottomOffset, rightOffset) == bottomOffset
                                        && Math.min(bottomOffset, topOffset) == bottomOffset
                                        && Math.min(leftOffset, bottomOffset) == bottomOffset)) {
                            tempMatrix.postScale(
                                    (((centerPoint.y - clipRect.bottom)) / (centerPoint.y - p2.y)),
                                    (((centerPoint.y - clipRect.bottom)) / (centerPoint.y - p2.y)), centerPoint.x, centerPoint.y)
                        }
                    }

                }
            }
        }
        imageMatrix = tempMatrix
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calc()
        drawClipRect(canvas)
    }

    private fun drawClipRect(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        clipPaint.style = Paint.Style.FILL
        clipPaint.color = Color.BLACK
        clipPaint.alpha = 0x70
        canvas.drawRect(0f, 0f, width.toFloat(), clipRect.top, clipPaint)
        canvas.drawRect(0f, clipRect.bottom, width.toFloat(), height.toFloat(), clipPaint)
        canvas.drawRect(0f, clipRect.top, clipRect.left, clipRect.bottom, clipPaint)
        canvas.drawRect(clipRect.right, clipRect.top, width.toFloat(), clipRect.bottom, clipPaint)

        clipPaint.color = Color.WHITE
        clipPaint.style = Paint.Style.STROKE
        clipPaint.alpha = 0xff
        clipPaint.isAntiAlias = true
        clipPaint.strokeWidth = strokeWidth.toFloat()
        canvas.drawRect(clipRect, clipPaint)
    }

    private fun calc() {
        val drawable = drawable
        if (computed || drawable == null) {
            return
        }
        computed = true
        clipRect.set((width - clipWidth) / 2f,
                (height - clipHeight) / 2f,
                (width + clipWidth) / 2f,
                (height + clipHeight) / 2f)
        centerPoint.set(clipRect.centerX(), clipRect.centerY())

        if (drawable is BitmapDrawable) {
            val bitmapWidth = drawable.bitmap.width
            val bitmapHeight = drawable.bitmap.height
            val initScale =
                    if (bitmapWidth > bitmapHeight * clipWidth / clipHeight) {
                        clipHeight * 1.0f / bitmapHeight
                    } else {
                        clipWidth * 1.0f / bitmapWidth
                    }

            val imageMidX = bitmapWidth * initScale / 2
            val imageMidY = bitmapHeight * initScale / 2
            scaleType = ScaleType.MATRIX
            tempMatrix.postScale(initScale, initScale)
            tempMatrix.postTranslate(centerPoint.x - imageMidX, centerPoint.y - imageMidY)
            imageMatrix = tempMatrix
        }
    }

    fun getClipBitmap(): Bitmap {
        isDrawingCacheEnabled = true
        buildDrawingCache()
        val finalBitmap = Bitmap.createBitmap(drawingCache,
                clipRect.left.toInt() + strokeWidth,
                clipRect.top.toInt() + strokeWidth,
                clipRect.width().toInt() - 2 * strokeWidth,
                clipRect.height().toInt() - 2 * strokeWidth)
        destroyDrawingCache()
        return finalBitmap
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun getLeftPointF(): PointF {
        val values = FloatArray(9)
        tempMatrix.getValues(values)
        val leftX = values[2]
        val leftY = values[5]

        return PointF(leftX, leftY)
    }

    private fun getRightPointF(): PointF {
        val values = FloatArray(9)
        tempMatrix.getValues(values)
        val leftX = values[2] + (drawable?.bounds?.width() ?: 0) * values[0]
        val leftY = values[5] + (drawable?.bounds?.height() ?: 0) * values[4]
        return PointF(leftX, leftY)
    }
}