package com.jj.base.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.jj.base.utils.ScreenUtil;
import com.jj.basiclib.R;

/**
 * Created by yangxl on 2016/9/29.
 */
public class CommonRadioGroup extends CustomRadioGroup {
    private static final int LEFT_PADDING_RADIO_TO_TEXT = ScreenUtil.dp2px(24);

    private int customLayoutId = 0;

    class HolderView {
        RadioButton radioButton;
        TextView radioLabel;
    }

    private CharSequence[] titles;

    public CommonRadioGroup(Context context) {
        this(context, null);
    }

    public CommonRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonRadioGroup);
        customLayoutId = a.getResourceId(R.styleable.CommonRadioGroup_itemLayout, 0);
        a.recycle();
    }

    public void setData(CharSequence[] titles) {
        boolean changed = false;
        if (titles != null && this.titles != null && this.titles.length == titles.length) {
            for (int i = 0; i < titles.length; i++) {
                if (!this.titles[i].equals(titles[i])) {
                    changed = true;
                    break;
                }
            }
        } else {
            changed = true;
        }
        if (changed) {
            removeAllViews();
            if (titles != null && titles.length > 0) {
                for (CharSequence title : titles) {
                    View child = createCommonRadioView(title);
                    addView(child);
                }
            }
            this.titles = titles;
        }
    }

    private View createCommonRadioView(CharSequence title) {
        View view = null;
        HolderView holder = new HolderView();
        if (customLayoutId == 0) {
            LinearLayout resultView = new LinearLayout(getContext());
            resultView.setOrientation(HORIZONTAL);

            final AppCompatRadioButton radioButton = new AppCompatRadioButton(getContext());
            LinearLayout.LayoutParams radioLp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioLp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            radioButton.setSupportButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.selector_color_radio_tint));
            resultView.addView(radioButton, radioLp);
            TextView radioLabel = new TextView(getContext());
            radioLabel.setText(title);
            radioLabel.setTextSize(DEAFULT_TEXT_SIZE);
            radioLabel.setTextColor(getResources().getColor(R.color.basic_black_text));
            LinearLayout.LayoutParams labelLp = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            labelLp.weight = 1;
            labelLp.leftMargin = LEFT_PADDING_RADIO_TO_TEXT;
            labelLp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            resultView.addView(radioLabel, labelLp);
            view = resultView;
            holder.radioButton = radioButton;
            holder.radioLabel = radioLabel;
        } else {
            View itemView = DataBindingUtil.inflate(LayoutInflater.from(getContext()), customLayoutId, this, false).getRoot();
            holder.radioButton = super.getRadioButtonFromView(itemView);
            TextView labelView = getLabelViewFromView(itemView);
            if (labelView == null) {
                labelView = holder.radioButton;
            }
            holder.radioLabel = labelView;
            holder.radioLabel.setText(title);
            view = itemView;
        }
        view.setTag(holder);
        view.setBackgroundResource(R.drawable.common_button_bg);
        return view;
    }

    @Override
    public RadioButton getRadioButtonFromView(View parentGroup) {
        if (parentGroup.getTag() != null && parentGroup.getTag() instanceof HolderView) {
            return ((HolderView) parentGroup.getTag()).radioButton;
        }
        return null;
    }

    public void check(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        check(findRadioButtonIdByTitle(title.toString()));
    }

    private int findRadioButtonIdByTitle(String title) {
        int childCnt = getChildCount();
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            if (child != null && child.getTag() != null && child.getTag() instanceof HolderView) {
                RadioButton radioButton = ((HolderView) child.getTag()).radioButton;
                TextView radioLabel = ((HolderView) child.getTag()).radioLabel;
                if (radioButton != null && radioLabel != null && title.equals(radioLabel.getText().toString())) {
                    return radioButton.getId();
                }
            }
        }
        return -1;
    }
}
