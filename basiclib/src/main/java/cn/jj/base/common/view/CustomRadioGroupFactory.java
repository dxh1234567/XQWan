package cn.jj.base.common.view;

import android.content.Context;


/**
 * 创建CustomRadioGrop布局的静态工厂类
 * Created by yangxl on 2016/9/29.
 */
public class CustomRadioGroupFactory {
    /**
     * 创建通用的RadioGroup
     * ________________________________
     * |   text              radioBtn |
     * |   text              radioBtn |
     * |   text              radioBtn |
     * --------------------------------
     */
    public static CustomRadioGroup createCommonRadioGroup(
            Context context, final CharSequence[] titles, final CharSequence selectedTitle,
            final CustomRadioGroup.OnSelectorCallBack listener) {

        final CommonRadioGroup result = new CommonRadioGroup(context);
        result.setDivderShown(false);
        result.setData(titles);
        result.check(selectedTitle);
        result.setOnCheckedChangeListener((group, checkedId) -> {
            if (listener != null) {
                listener.onCallBack(group.getSelectedIndex());
            }
        });
        return result;
    }
}
