
package com.jj.base.utils;

import android.graphics.Paint;
import android.graphics.Rect;

public class FontUtil {
    private static Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
    private static Rect mCalcTextSizeRect = new Rect();

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = mCalcTextSizeRect;
        r.set(0, 0, 0, 0);
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    public static float getLineHeight(Paint paint) {
        return getLineHeight(paint, mFontMetrics);
    }

    public static float getLineHeight(Paint paint, Paint.FontMetrics fontMetrics) {
        paint.getFontMetrics(fontMetrics);
        return fontMetrics.descent - fontMetrics.ascent;
    }

    public static float getLineSpacing(Paint paint) {
        return getLineSpacing(paint, mFontMetrics);
    }

    public static float getLineSpacing(Paint paint, Paint.FontMetrics fontMetrics) {
        paint.getFontMetrics(fontMetrics);
        return fontMetrics.ascent - fontMetrics.top + fontMetrics.bottom;
    }


    public static FontSize calcTextSize(Paint paint, String demoText) {

        Rect r = mCalcTextSizeRect;
        r.set(0, 0, 0, 0);
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return new FontSize(mCalcTextSizeRect.width(), mCalcTextSizeRect.height());
    }

    public static class FontSize {
        public int width;
        public int height;

        public FontSize(int w, int h) {
            width = w;
            height = h;
        }

        @Override
        public String toString() {
            return "FontSize(" + width + ", " + height + ")";
        }
    }
}
