/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jj.base.common.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Base class for a View which shows a floating UI on top of the launcher UI.
 */
public abstract class AbstractFloatingView extends LinearLayout {

    @IntDef(flag = true, value = {
            TYPE_POPUP_CONTAINER_WITH_ARROW,
            TYPE_POPUP_MESSAGE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatingViewType {
    }

    public static final int TYPE_POPUP_CONTAINER_WITH_ARROW = 1 << 0;
    public static final int TYPE_POPUP_MESSAGE = 1 << 1;
    protected boolean mIsOpen;

    public AbstractFloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * We need to handle touch events to prevent them from falling through to the workspace below.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    public final void close(boolean animate) {
        handleClose(animate);
    }

    protected abstract void handleClose(boolean animate);

    /**
     * Any additional view (outside of this container) where touch should be allowed while this
     * view is visible.
     */
    public View getExtendedTouchView() {
        return null;
    }

    public final boolean isOpen() {
        return mIsOpen;
    }

    protected abstract boolean isOfType(@FloatingViewType int type);

    protected static <T extends AbstractFloatingView> T getOpenView(ViewGroup container,
                                                                    @FloatingViewType int type) {
        // Iterate in reverse order. AbstractFloatingView is added later to the parent,
        // and will be one of the last views.
        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            View child = container.getChildAt(i);
            if (child instanceof AbstractFloatingView) {
                AbstractFloatingView view = (AbstractFloatingView) child;
                if (view.isOfType(type) && view.isOpen()) {
                    return (T) view;
                }
            }
        }
        return null;
    }

    public static void closeOpenContainer(ViewGroup container, @FloatingViewType int type) {
        AbstractFloatingView view = getOpenView(container, type);
        if (view != null) {
            view.close(true);
        }
    }

    public static void closeAllOpenViews(ViewGroup container, boolean animate) {
        // Iterate in reverse order. AbstractFloatingView is added later to the parent,
        // and will be one of the last views.
        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            View child = container.getChildAt(i);
            if (child instanceof AbstractFloatingView) {
                ((AbstractFloatingView) child).close(animate);
            }
        }
    }

    public static void closeAllOpenViews(ViewGroup container) {
        closeAllOpenViews(container, true);
    }

    public static AbstractFloatingView getTopOpenView(ViewGroup container) {
        return getOpenView(container, TYPE_POPUP_CONTAINER_WITH_ARROW);
    }
}
