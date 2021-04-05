package com.jj.base.common.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import org.jetbrains.annotations.NotNull;

public class VerticalCenterSpan extends ReplacementSpan implements ParcelableSpan {

    private int fontSizePx;
    private int color;

    public VerticalCenterSpan(int fontSizePx, int color) {
        this.fontSizePx = fontSizePx;
        this.color = color;
    }

    @Override
    public int getSize(@NotNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        text = text.subSequence(start, end);
        Paint p = getCustomTextPaint(paint);
        return (int) p.measureText(text.toString());
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NotNull Paint paint) {
        text = text.subSequence(start, end);

        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int height = fontMetricsInt.descent - fontMetricsInt.ascent;
        float cY = fontMetricsInt.ascent + height;

        Paint p = getCustomTextPaint(paint);
        p.setColor(color);
        Paint.FontMetricsInt fm = p.getFontMetricsInt();
        int newH = fm.descent - fm.ascent;
        float newCY = fm.ascent + newH;

        canvas.drawText(text.toString(), x, y - (cY - newCY), p);
    }

    private TextPaint getCustomTextPaint(Paint srcPaint) {
        TextPaint paint = new TextPaint(srcPaint);
        paint.setTextSize(fontSizePx);
        return paint;
    }

    @Override
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    public int getSpanTypeIdInternal() {
        return 300002;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeInt(fontSizePx);
    }
}