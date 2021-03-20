package cn.jj.base.common.input.widget.container;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.jj.base.common.input.util.KeyboardUtil;
import cn.jj.base.utils.LogUtil;
import cn.jj.basiclib.R;

/**
 * 聊天输入面板的根布局，继承自LinearLayout
 * <p>
 * 利用键盘弹出时候，布局大小改变的特性，计算出键盘高度，并监听键盘弹出收起的事件，因此，不适用于全屏ACTIVITY
 * <p>
 * 注意：大小需要和Activity根布局大小相同，这样才能监测到高度的改变
 * 透明状态栏需要设置fitsSystemWindows="true"
 * 非透明状态栏会压缩背景
 */
public class InputPanelRoot extends LinearLayout {

    /**
     * 上次onMeasure时候的高度。默认值-1
     */
    private int mOldHeight = -1;

    private Rect rect = new Rect();

    private InputPanelContainer panel;

    public InputPanelRoot(Context context) {
        super(context);
    }

    public InputPanelRoot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputPanelRoot(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        beforeMeasure(MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 根据高度计算各种情况
     * todo 一些特殊情况下（全屏、透明状态栏等），height不会发生变化，需要重新计算 现在全部重新计算
     * todo 利用高度差计算键盘高度，目前不知道是否有兼容问题
     *
     * @param height 高度
     */
    private void beforeMeasure(int height) {
        //获取PanelContainer
        panel = getPanelContainer(this);
        if (panel == null) {
            return;
        }

        // 目前全部重新计算
        getWindowVisibleDisplayFrame(rect);
        height = rect.bottom - rect.top;
        if (height <= 0) {
            return;
        }

        //第一次，记录高度值
        if (mOldHeight < 0) {
            mOldHeight = height;
            return;
        }

        //计算两次的高度差
        int offset = mOldHeight - height;
        mOldHeight = height;
        if (offset == 0) {
            return;
        }

        //一些特殊情况判断，底部虚拟按键、一些状态栏的变化等引起的布局变化，通过设定最小值的方式排除
        if (Math.abs(offset) < getResources().getDimensionPixelSize(R.dimen.min_keyboard_height)) {
            return;
        }

        onKeyboardChanged(offset);
    }

    /**
     * 键盘发生变化
     */
    private void onKeyboardChanged(int offset) {
        //储存键盘高度，返回是否有变化，如果有变化，需要改变表情包面板的高度
        boolean changed = KeyboardUtil.saveKeyboardHeight(getContext(), Math.abs(offset));
        if (changed) {
            int validPanelHeight = KeyboardUtil.getValidPanelHeight(getContext());
            if (panel.getHeight() != validPanelHeight) {
                panel.refreshHeight(validPanelHeight);
            }
        }

        // offset > 0，高度变小，认为是键盘弹出
        panel.setKeyboardShowing(offset > 0);
        if (offset > 0) {
            panel.setHide(true);
        } else if (!panel.isHide()) {
            //当键盘没有显示且子布局应该可见
            panel.setVisibility(VISIBLE);
        }
        if (listener != null) {
            listener.onKeyboardShowing(offset > 0);
        }
    }

    /**
     * 在子VIEW中查找InputPanelContainer
     */
    private InputPanelContainer getPanelContainer(View view) {
        if (panel != null) {
            return panel;
        }

        if (view instanceof InputPanelContainer) {
            panel = (InputPanelContainer) view;
            return panel;
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                InputPanelContainer v = getPanelContainer(((ViewGroup) view).getChildAt(i));
                if (v != null) {
                    panel = v;
                    return panel;
                }
            }
        }
        return null;
    }

    /**
     * 切换面板状态
     */
    public void switchPanelAndKeyboard(View focusView) {
        boolean switchToKeyboard = isPanelVisibility();
        if (switchToKeyboard) {
            showKeyboard(focusView);
        } else {
            showPanel(focusView);
        }
    }

    /**
     * 展示InputPanelContainer
     */
    public void showPanel(View focusView) {
        if (panel == null) {
            return;
        }

        final Activity activity = (Activity) getContext();
        if (activity.getCurrentFocus() != null) {
            KeyboardUtil.hideKeyboard(focusView);
        }
        panel.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏所有
     */
    public void hideAll(View focusView) {
        if (panel == null) {
            return;
        }

        KeyboardUtil.hideKeyboard(focusView);
        panel.setVisibility(View.GONE);
    }

    /**
     * 当所在父布局因为各种原因隐藏时，可能出现bug，提供清理记录的高度值的方法，解决这个问题
     */
    public void clearRecordHeight(){
        //隐藏时，清空记录的高度值
        mOldHeight = -1;
    }

    /**
     * 弹出键盘
     */
    public void showKeyboard(View focusView) {
        KeyboardUtil.showKeyboard(focusView);
    }

    public boolean isPanelVisibility() {
        if (panel == null) {
            return false;
        }
        return panel.getVisibility() == View.VISIBLE;
    }

    private KeyboardUtil.OnKeyboardShowingListener listener;

    public void setOnKeyboardShowingListener(KeyboardUtil.OnKeyboardShowingListener listener) {
        this.listener = listener;
    }
}
