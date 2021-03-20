package cn.jj.base.common.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by yangxl on 2017/12/6.
 */

public class RippleAnimDrawable extends Drawable {
    private final BezierCurve mCurve;
    private Paint paint;
    private float aniValue = 0f;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private int color;

    public RippleAnimDrawable() {
        color = Color.parseColor("#4480F7");
        paint = new Paint();
        paint.setColor(color);

        float[] controls = new float[]{
                0f, 0f,
                .30f, 0.9f,
                .33f, 1f,
                .35f, 0.75f,
                .95f, 0f,
                1f, 0f
        };
        mCurve = new BezierCurve(controls, 300);
    }

    public void update(float value) {
        this.aniValue = value;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        int width = r.width();
        int centerX = r.centerX();
        int centerY = r.centerY();
        int alpha;
        alpha = (int) (255 * 0.2f * mCurve.value_y(aniValue));

        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 255) {
            alpha = 255;
        }
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawCircle(centerX, centerY, width * (interpolator.getInterpolation(aniValue) * 0.8f + 0.4f) / 2, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
