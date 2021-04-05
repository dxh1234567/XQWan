package com.jj.base.common.input.widget.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jj.base.common.input.panel.emoticon.view.EmoticonPanel;
import com.jj.base.common.input.util.KeyboardUtil;

/**
 * 此布局保持和键盘高度相同，将表情包面板放到此布局中
 */
public class InputPanelContainer extends LinearLayout {

    /**
     * 是否可见的真正标志
     */
    private boolean isHide = false;

    /**
     * 键盘是否显示的标志
     */
    private boolean isKeyboardShowing = false;

    private EmoticonPanel emoticonPanel;

    public InputPanelContainer(Context context) {
        super(context);
    }

    public InputPanelContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputPanelContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] beforeOnMeasure = beforeOnMeasure(widthMeasureSpec,
                heightMeasureSpec);
        super.onMeasure(beforeOnMeasure[0], beforeOnMeasure[1]);
    }

    /**
     * 在Measure前，如果状态是隐藏，设置布局不可见，同时将布局大小改为0
     * <p>
     * 在根布局中判断键盘弹出，并设置isHide = true
     *
     * @see InputPanelRoot#onKeyboardChanged(int)
     */
    private int[] beforeOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isHide) {
            setVisibility(View.GONE);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        } else {
            int validPanelHeight = KeyboardUtil.getValidPanelHeight(getContext());
            if (MeasureSpec.getSize(heightMeasureSpec) != validPanelHeight) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(validPanelHeight, MeasureSpec.EXACTLY);
            }
        }

        final int[] processedMeasureWHSpec = new int[2];
        processedMeasureWHSpec[0] = widthMeasureSpec;
        processedMeasureWHSpec[1] = heightMeasureSpec;

        return processedMeasureWHSpec;
    }

    @Override
    public void setVisibility(int visibility) {
        if (beforeSetVisibility(visibility)) {
            return;
        }
        super.setVisibility(visibility);
    }

    /**
     * 设置状态前进行判断
     * <p>
     * 如果设置布局可见，将isHide置为false，但若此时键盘可见，返回true
     *
     * @see InputPanelRoot#showPanel(View)
     * @see InputPanelRoot#onKeyboardChanged(int)
     */
    private boolean beforeSetVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            this.isHide = false;
        }

        if (visibility == getVisibility()) {
            return true;
        }

        if (isKeyboardShowing && visibility == View.VISIBLE) {
            return true;
        }

        return false;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isKeyboardShowing() {
        return isKeyboardShowing;
    }

    public void setKeyboardShowing(boolean keyboardShowing) {
        isKeyboardShowing = keyboardShowing;
    }

    public void refreshHeight(int validPanelHeight) {
        if (isInEditMode()) {
            return;
        }
        if (getHeight() == validPanelHeight) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    validPanelHeight);
            setLayoutParams(layoutParams);
        } else {
            layoutParams.height = validPanelHeight;
            requestLayout();
        }
    }
}
