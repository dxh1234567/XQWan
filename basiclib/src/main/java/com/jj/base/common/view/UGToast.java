package com.jj.base.common.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.jj.base.UIUtils;
import com.jj.base.utils.Utility;
import com.jj.basiclib.R;

/**
 * Created By duXiaHui
 * on 2021/1/30
 */
public class UGToast extends Toast {

    private UGToast(Context context, String msg, int duration, boolean isSuccess) {
        super(context);
        setDuration(duration);
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_success_tip, null);
//        RelativeLayout relativeLayout = contentView.findViewById(R.id.relative);
        TextView textView = contentView.findViewById(R.id.text);
//        if (isSuccess) {
//            relativeLayout.setBackgroundResource(R.drawable-xxhdpi.tips_success_bg);
//        } else {
//            relativeLayout.setBackgroundResource(R.drawable-xxhdpi.tips_error_bg);
//        }
        textView.setTextColor(Color.WHITE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(msg);
        setView(contentView);
    }

    public static void showToast(@StringRes int resStr, boolean isSuccess) {
        showToast(UIUtils.getString(resStr), isSuccess);
    }

    private static void showToast(String text, boolean isSuccess) {
        new UGToast(Utility.getApplication(), text, LENGTH_SHORT, isSuccess).show();
    }

    public static void showToast(String text) {
        showToast(text, false);
    }

}
