package cn.jj.base.common.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.jj.basiclib.R;
import cn.jj.base.utils.ViewUtil;


/**
 * Created by yangxl on 2016/12/21.
 */

public class LoadingView extends View {
    private static final String TAG = "LoadingView";

    private static final int TICK_COUNT = 12;
    private static final float TICK_HIGHT_FOCTOR = 8 / 25.0f;
    private static final float TICK_WIDTH_FOCTOR = 2 / 25.0f;
    private static final int ANIMATOR_DURATION = 800;

    private int mRadio = 100;
    private int mTickHight;
    private int mTickWidth;
    private int mLightTick = 0;
    private int[] mAlphas;

    private PointF mCenterPoint;
    private Paint mTickPaint;
    private int mTickColor = Color.WHITE;

    private ValueAnimator valueAnimator;
    private TimeInterpolator interpolator = new LinearInterpolator();

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mTickColor = a.getColor(R.styleable.LoadingView_tickColor, mTickColor);
        a.recycle();
    }

    private void initValue() {
        if (mRadio == 0 || mCenterPoint == null) {
            mTickPaint = new Paint();
            mTickPaint.setAntiAlias(true);
            mTickPaint.setStyle(Paint.Style.FILL);
            mTickPaint.setDither(true);
            mTickPaint.setColor(mTickColor);

            mAlphas = new int[TICK_COUNT];
            int foctor = (255 - 10) / TICK_COUNT;
            for (int i = 0; i < TICK_COUNT; i++) {
                mAlphas[i] = 255 - (i + 1) * foctor;
            }
            mRadio = (int) (Math.min(getWidth(), getHeight()) / (2.0f * (1 + TICK_HIGHT_FOCTOR)));
            mTickHight = (int) (mRadio * TICK_HIGHT_FOCTOR);
            mTickWidth = (int) (mRadio * TICK_WIDTH_FOCTOR);
            mTickPaint.setStrokeWidth(mTickWidth);
            mCenterPoint = new PointF(getWidth() / 2.0f, getHeight() / 2.0f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        canvas.save(); //记录画布状态
        canvas.rotate((360 / 2), 0, 0);
        float rAngle = -360 / (TICK_COUNT * 1.0f);
        for (int index = 0; index < TICK_COUNT; index++) {
            canvas.rotate(rAngle, 0, 0);
            mTickPaint.setColor(mTickColor);
            mTickPaint.setAlpha(mAlphas[(index + mLightTick) % TICK_COUNT]);
            canvas.drawLine(0, -mRadio - mTickHight, 0, -mRadio, mTickPaint);
        }
        canvas.restore();
    }

    public void startAnimator() {
        cancelAnimator();
        initValue();
        valueAnimator = ValueAnimator.ofInt(0, TICK_COUNT).setDuration(ANIMATOR_DURATION);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLightTick = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
        ViewUtil.viewPostInvalidateOnAnimation(this);
    }

    private void cancelAnimator() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    public void setTickColor(int tickColor) {
        mTickColor = tickColor;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            cancelAnimator();
        } else {
            startAnimator();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != VISIBLE) {
            cancelAnimator();
        } else {
            startAnimator();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimator();
        super.onDetachedFromWindow();
    }

}
