package com.jj.xqwan.view;

/**
 * Created By duXiaHui
 * on 2021/1/30
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.Nullable;

import com.jj.xqwan.R;


public class UGLoadingView extends androidx.appcompat.widget.AppCompatImageView {

    /**
     * 总共要旋转的角度
     */
    private int maxRotate = 720;

    /**
     * 边框的粗细
     */
    private int borderWidth;

    /**
     * 边框的颜色
     */
    private int borderColor;

    /**
     * 边框的最大长度，这个是划过的角度
     */
    private int maxAngle;

    /**
     * 画笔
     */
    private Paint paint;

    /**
     * 绘制的区域
     */
    private RectF contentRectF;

    /**
     * 旋转动画
     */
    private ValueAnimator valueAnimator;

    /**
     * 动画时长
     */
    private int duration;

    /**
     * 偏移的角度
     */
    private int offsetAngle;

    /**
     * 进度划过的角度
     */
    private float progressAngle;

    public UGLoadingView(Context context) {
        this(context, null);
    }

    public UGLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UGLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contentRectF = new RectF();
        // 获取自定义属性的值
        initProperties(context, attrs);
        // 初始化画笔
        initPaint();
    }

    /**
     * 初始化自定义属性
     *
     * @param context 上下文
     * @param attrs   属性集合
     */
    private void initProperties(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UGLoadingView);
        borderWidth = typedArray.getDimensionPixelOffset(R.styleable.UGLoadingView_borderWidth,
                context.getResources().getDimensionPixelOffset(R.dimen.default_loading_border_width));
        borderColor = typedArray.getColor(R.styleable.UGLoadingView_borderColor,
                Color.BLACK);
        maxAngle = typedArray.getInt(R.styleable.UGLoadingView_maxAngle, 100);
        duration = typedArray.getInt(R.styleable.UGLoadingView_duration, 2000);
        typedArray.recycle();


    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        // 去除锯齿
        paint.setAntiAlias(true);
        paint.setDither(true);
        // 画笔颜色
        paint.setColor(borderColor);
        // 设置画笔样式为边框
        paint.setStyle(Paint.Style.STROKE);
        // 设置画笔的宽度
        paint.setStrokeWidth(borderWidth);
        // 设置画笔的两端样式，这里是圆滑
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 初始化旋转动画
     */
    private void initValueAnimator() {
        if (valueAnimator != null) {
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(0, maxRotate);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            // 计算偏移值
            offsetAngle = (int) (-90 + value);
            // 计算进度条的宽度变化
            if (value <= maxRotate / 2) {
                progressAngle = (int) (maxAngle * (valueAnimator.getAnimatedFraction() * 2));
            } else {
                progressAngle = (int) (maxAngle * (2 - valueAnimator.getAnimatedFraction() * 2));
            }
            if (progressAngle == 0) {
                progressAngle = 0.01f;
            }
            invalidate();
        });
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;
        int halfBorderWidth = borderWidth / 2;
        // 计算半径
        float radius = centerX > centerY ? centerY : centerX;
        // 计算要绘制的区域
        contentRectF.set(
                centerX - radius + halfBorderWidth,
                centerY - radius + halfBorderWidth,
                centerX + radius - halfBorderWidth,
                centerY + radius - halfBorderWidth);

        // 计算要设置的padding，防止图片的会压到圆上
        calculateCirclePadding(centerX > centerY ? centerX : centerY);
    }

    /**
     * 计算最小padding
     *
     * @param radius 最大的半径，因为不保证一定是正方形，所以用最大的半径来进行计算
     */
    private void calculateCirclePadding(int radius) {
        // 得到边长院内正方形的边长的一半
        int rectLength = (int) Math.sqrt(radius * radius >> 1);
        // 用现在的padding和已经设置的padding进行比较
        // padding的最小值是圆的半径减去radius
        int minPadding = radius - rectLength;
        setPadding(
                Math.max(getPaddingLeft(), minPadding),
                Math.max(getPaddingTop(), minPadding),
                Math.max(getPaddingRight(), minPadding),
                Math.max(getPaddingBottom(), minPadding)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 这里要画圆弧
        canvas.drawArc(contentRectF, offsetAngle, progressAngle, false, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initValueAnimator();
        // 开始旋转动画
        if (!valueAnimator.isStarted()) {
            valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 停止旋转动画
        if (valueAnimator.isStarted()) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }
}
