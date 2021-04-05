package com.jj.base.common.anim;

import android.view.View;

import static com.jj.base.utils.Constant.ATLEAST_LOLLIPOP;

/**
 * Created by yangxl on 2018/1/25.
 * 状态改变动画，只适用于pre-Lollipop,Lollipop及以上版本采用StateListAnimator
 */

public abstract class StateAnimator {
    public boolean pressed;
    public boolean enabled;
    public boolean checked;
    public boolean activated;
    public boolean selected;
    public View target;

    public StateAnimator(View target) {
        if (ATLEAST_LOLLIPOP) {
            throw new IllegalArgumentException("Lollipop及以上版本请采用 StateListAnimator 代替");
        }
        this.target = target;
    }

    public void stateChanged(int[] states) {
        boolean newPressed = false, newEnabled = false,
                newChecked = false, newActivated = false,
                newSelected = false;
        for (int state : states) {
            if (state == android.R.attr.state_enabled) {
                newEnabled = true;
            } else if (state == android.R.attr.state_pressed) {
                newPressed = true;
            } else if (state == android.R.attr.state_checked) {
                newChecked = true;
            } else if (state == android.R.attr.state_activated) {
                newActivated = true;
            } else if (state == android.R.attr.state_selected) {
                newSelected = true;
            }
        }
        if (pressed != newPressed) {
            pressed = newPressed;
            onPressedChanged();
        }
        if (enabled != newEnabled) {
            enabled = newEnabled;
            onEnabledChanged();
        }
        if (checked != newChecked) {
            checked = newChecked;
            onCheckedChanged();
        }
        if (activated != newActivated) {
            activated = newActivated;
            onActivatedChanged();
        }
        if (selected != newSelected) {
            selected = newSelected;
            onSelectedChanged();
        }
    }

    protected abstract void onSelectedChanged();

    protected abstract void onActivatedChanged();

    protected abstract void onCheckedChanged();

    protected abstract void onEnabledChanged();

    protected abstract void onPressedChanged();

    public abstract void cancel();
}
