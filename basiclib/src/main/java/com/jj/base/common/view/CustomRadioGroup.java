package com.jj.base.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jj.base.utils.LogUtil;
import com.jj.base.utils.ScreenUtil;
import com.jj.base.utils.ViewUtil;
import com.jj.basiclib.R;


/**
 * 参考radiogroup实现一个自定义的
 * Created by yangxl on 2016/8/24.
 */
public class CustomRadioGroup extends LinearLayout {
    private static final String TAG = "CustomRadioGroup";

    protected static final int PADDING_RIGHT = ScreenUtil.dp2px(24);
    protected static final int PADDING_LEFT = ScreenUtil.dp2px(18);
    protected static final int PADDING_TOP = ScreenUtil.dp2px(10);
    protected static final int PADDING_BOTTOM = ScreenUtil.dp2px(10);
    protected static final int DEAFULT_TEXT_SIZE = 14;
    protected static final String TAG_DIVDER = "divder";

    // holds the checked id; the selection is empty by default
    protected int mCheckedId = -1;
    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    private boolean mIsShowDivderLine = true;
    LayoutParams mRadioLp_V =
            new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    LayoutParams mDivderLp_V =
            new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    LayoutParams mRadioLp_H =
            new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    LayoutParams mDivderLp_H =
            new LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);


    /**
     * 一个radioButton的容器的点击监听
     */
    private OnClickListener mCustomRatioViewClickListener;


    public CustomRadioGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        mCustomRatioViewClickListener = v -> {
            RadioButton radioButton = getRadioButtonFromView(v);
            if (radioButton != null) {
                check(radioButton.getId());
            }
        };
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }


    public void setDivderShown(boolean isShown) {
        mIsShowDivderLine = isShown;
    }

    private View createDivderView() {
        View divderView = new View(getContext());
        divderView.setBackgroundColor(getResources().getColor(R.color.basic_divider));
        divderView.setTag(TAG_DIVDER);
        return divderView;
    }

    private boolean isDivderView(View view) {
        return view != null && TAG_DIVDER.equals(view.getTag());
    }

    public void setRadioButtonCheckedByIndex(int selectedIndex) {
        RadioButton radioButton = getRadioButtonByIndex(selectedIndex);
        if (radioButton != null) {
            check(radioButton.getId());
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child, getOrientation() == VERTICAL ? mRadioLp_V : mRadioLp_H);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (isDivderView(child)) {
            super.addView(child, index, params);
            return;
        }
        RadioButton radioButton = getRadioButtonFromView(child);
        if (radioButton != null) {
            if (radioButton.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(radioButton.getId());
            }
            child.setOnClickListener(mCustomRatioViewClickListener);
            child.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
        }
        if (mIsShowDivderLine) {
            if (getChildCount() != 0) {
                addView(createDivderView(), getOrientation() == VERTICAL ? mDivderLp_V : mDivderLp_H);
            }
        }
        super.addView(child, index, params);
    }

    public RadioButton getRadioButtonFromView(View parentGroup) {
        if (parentGroup instanceof RadioButton) {
            return (RadioButton) parentGroup;
        }
        if (parentGroup instanceof ViewGroup) {
            final int childCount = ((ViewGroup) parentGroup).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) parentGroup).getChildAt(i);
                if (child instanceof RadioButton) {
                    return (RadioButton) child;
                }
                if (child instanceof ViewGroup) {
                    RadioButton radioButton = getRadioButtonFromView(child);
                    if (radioButton != null) {
                        return radioButton;
                    }
                }
            }
        }
        return null;
    }

    //todo 暂如此
    protected TextView getLabelViewFromView(View parentGroup) {
        if (parentGroup instanceof TextView) {
            return (TextView) parentGroup;
        }
        if (parentGroup instanceof ViewGroup) {
            final int childCount = ((ViewGroup) parentGroup).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) parentGroup).getChildAt(i);
                if (child instanceof TextView && !(child instanceof RadioButton)) {
                    return (TextView) child;
                }
                if (child instanceof ViewGroup) {
                    TextView labelView = getLabelViewFromView(child);
                    if (labelView != null) {
                        return labelView;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 对radiobutton状态的改变都通过check（）来改变
     */
    public void check(int id) {
        if (id != -1 && (id == mCheckedId)) {
            return;
        }
        mProtectFromCheckedChange = true;
        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }
        mProtectFromCheckedChange = false;

        setCheckedId(id);
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, id);
        }
    }

    private void setCheckedId(int id) {
        LogUtil.i(TAG, "setCheckedId： id=" + id);
        mCheckedId = id;
    }


    /**
     * 获取选中的radioButton在radiogroup中的引导值,从0开始
     */
    public int getSelectedIndex() {
        int childCnt = getChildCount();
        int index = -1;
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            if (isDivderView(child)) {
                continue;
            }
            RadioButton radioButton = getRadioButtonFromView(child);
            if (radioButton != null) {
                index++;
            }
            if (radioButton.getId() == mCheckedId) {
                return index;
            }
        }
        return 0;
    }

    public TextView getRadioLabelByIndex(int radioLabelIndex) {
        int childCnt = getChildCount();
        int index = -1;
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            if (isDivderView(child)) {
                continue;
            }
            TextView radioLabel = getLabelViewFromView(child);
            if (radioLabel != null) {
                index++;
            }
            if (index >= radioLabelIndex) {
                return radioLabel;
            }
        }
        return null;
    }

    public RadioButton getRadioButtonByIndex(int radioBtnIndex) {
        int childCnt = getChildCount();
        int index = -1;
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            if (isDivderView(child)) {
                continue;
            }
            RadioButton radioButton = getRadioButtonFromView(child);
            if (radioButton != null) {
                index++;
            }
            if (index >= radioBtnIndex) {
                return radioButton;
            }
        }
        return null;
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof RadioButton) {
            ((RadioButton) checkedView).setChecked(checked);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomRadioGroup.LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof CustomRadioGroup.LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CustomRadioGroup.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CustomRadioGroup.class.getName());
    }


    public static class LayoutParams extends LinearLayout.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }


        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CustomRadioGroup group, int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(CustomRadioGroup.this, id);
            }
        }
    }


    private class PassThroughHierarchyChangeListener implements
            OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        public void onChildViewAdded(View parent, View child) {
            RadioButton radioButton = getRadioButtonFromView(child);
            if (parent == CustomRadioGroup.this && radioButton != null) {
                int id = radioButton.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = ViewUtil.generateViewId();
                    radioButton.setId(id);
                }
                radioButton.setOnCheckedChangeListener(
                        mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        public void onChildViewRemoved(View parent, View child) {
            RadioButton radioButton = getRadioButtonFromView(child);
            if (parent == CustomRadioGroup.this && radioButton != null) {
                radioButton.setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }


    public interface OnSelectorCallBack {
        void onCallBack(int selectedIndex);
    }

}
