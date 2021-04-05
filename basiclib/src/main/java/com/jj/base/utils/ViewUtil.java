package com.jj.base.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import static com.jj.base.utils.Constant.ATLEAST_JB_MR1;

public class ViewUtil {
    private static final float[] sPoint = new float[2];

    public static void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    public static void setText(TextView view, int resid) {
        String str = view.getResources().getString(resid);
        setText(view, str);
    }

    public static void setText(TextView view, String str) {
        if (!str.equals(view.getText().toString())) {
            view.setText(str);
        }
    }

    public static void viewPostInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 16)
            view.postInvalidateOnAnimation();
        else
            view.invalidate();
    }

    public static void removeOnGlobalLayoutListener(
            View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void removeFromParent(View view) {
        if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static float centerX(View view) {
        return view.getX() + view.getWidth() / 2;
    }

    public static float centerY(View view) {
        return view.getY() + view.getHeight() / 2;
    }

    /**
     * Given a coordinate relative to the descendant, find the coordinate in a parent view's
     * coordinates.
     *
     * @param descendant        The descendant to which the passed coordinate is relative.
     * @param ancestor          The root view to make the coordinates relative to.
     * @param coord             The coordinate that we want mapped.
     * @param includeRootScroll Whether or not to account for the scroll of the descendant:
     *                          sometimes this is relevant as in a child's coordinates within the descendant.
     * @return The factor by which this descendant is scaled relative to this DragLayer. Caution
     * this scale factor is assumed to be equal in X and Y, and so if at any point this
     * assumption fails, we will need to return a pair of scale factors.
     */
    public static float getDescendantCoordRelativeToAncestor(
            View descendant, View ancestor, int[] coord, boolean includeRootScroll) {
        sPoint[0] = coord[0];
        sPoint[1] = coord[1];

        float scale = 1.0f;
        View v = descendant;
        while (v != ancestor && v != null) {
            // For TextViews, scroll has a meaning which relates to the text position
            // which is very strange... ignore the scroll.
            if (v != descendant || includeRootScroll) {
                sPoint[0] -= v.getScrollX();
                sPoint[1] -= v.getScrollY();
            }

            v.getMatrix().mapPoints(sPoint);
            sPoint[0] += v.getLeft();
            sPoint[1] += v.getTop();
            scale *= v.getScaleX();
            v = (View) v.getParent();
        }

        coord[0] = Math.round(sPoint[0]);
        coord[1] = Math.round(sPoint[1]);
        return scale;
    }


    public static float getDescendantRectRelativeToAncestor(View descendant, View ancestor, Rect r) {
        int[] coord = new int[]{0, 0};

        float scale = ViewUtil.getDescendantCoordRelativeToAncestor(
                descendant,
                ancestor,
                coord,
                false);

        r.set(coord[0], coord[1],
                (int) (coord[0] + scale * descendant.getMeasuredWidth()),
                (int) (coord[1] + scale * descendant.getMeasuredHeight()));
        return scale;
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(
            TextView view,
            @Nullable Drawable left,
            @Nullable Drawable top,
            @Nullable Drawable right,
            @Nullable Drawable bottom) {
        if (ATLEAST_JB_MR1) {
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
        } else {
            if (left != null) {
                left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
            }
            if (right != null) {
                right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
            }
            if (top != null) {
                top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
            }
            if (bottom != null) {
                bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
            }
            view.setCompoundDrawables(left, top, right, bottom);
        }
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(
            TextView view,
            @DrawableRes int start,
            @DrawableRes int top, @DrawableRes int end, @DrawableRes int bottom) {
        final Context context = view.getContext();
        setCompoundDrawablesRelativeWithIntrinsicBounds(
                view,
                start != 0 ? ContextCompat.getDrawable(context, start) : null,
                top != 0 ? ContextCompat.getDrawable(context, top) : null,
                end != 0 ? ContextCompat.getDrawable(context, end) : null,
                bottom != 0 ? ContextCompat.getDrawable(context, bottom) : null);
    }
}