package cn.jj.base.common.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jj.basiclib.R;
import cn.jj.base.common.ThreadManager;
import cn.jj.base.utils.ScreenUtil;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangxl on 2018/4/11.
 */

public class ReboundToast extends Dialog {
    public static final String TAG = "ReboundToast";
    public static final float TOAST_WIDTH_RATIO = 320.f / 360;
    public static final float TOAST_DIMENSION_RATIO = 60.f / 320;

    static final long SHORT_DURATION_TIMEOUT = 1000;
    static final long LONG_DURATION_TIMEOUT = 5000;

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private FrameLayout container;
    private ImageView defaultImageView;
    private TextView defaultTextView;

    private Spring spring;
    private SimpleSpringListener springListener;
    private boolean autoDimiss;
    private long delayTime;
    private int toastHeight;
    private Runnable dimissRunnable;

    private ReboundToast(Context context) {
        this(context, R.style.custom_alter_dialog_style);
    }

    private ReboundToast(Context context, int themeResId) {
        super(context, themeResId);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0f;
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.windowAnimations = 0;
        getWindow().setAttributes(lp);
        initView();
        dimissRunnable = () -> {
            if (isShowing()) {
                dismiss();
            }
        };
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.custom_rebound_toast_layout, null);
        container = view.findViewById(R.id.container);
        defaultImageView = view.findViewById(R.id.message_icon);
        defaultTextView = view.findViewById(R.id.message_text);

        SpringSystem springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 1));
        springListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float ddd = toastHeight * (1 - value);
                container.setTranslationY(ddd);
            }
        };

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
        lp.width = (int) (ScreenUtil.getScreenWidth() * TOAST_WIDTH_RATIO);
        toastHeight = lp.height = (int) (lp.width * TOAST_DIMENSION_RATIO);
        container.setLayoutParams(lp);
        setContentView(view);
    }

    public void setMessageText(String contentText) {
        defaultTextView.setText(contentText);
    }

    public void setMessageIcon(Drawable icon) {
        defaultImageView.setImageDrawable(icon);
    }

    public void setMessageView(View view) {
        container.removeAllViews();
        container.setVisibility(View.VISIBLE);
        container.addView(view);
    }

    public void setMessageClickListener(View.OnClickListener listener) {
        if (listener != null) {
            container.setOnClickListener((view) -> {
                listener.onClick(view);
                dismiss();
            });
        } else {
            container.setOnClickListener(null);
        }
    }

    @Override
    public void show() {
        if (getContext() instanceof Activity && ((Activity) getContext()).isDestroyed()) {
            dismiss();
            return;
        }
        try {
            super.show();
            container.setTranslationY(toastHeight * 0.85f);
            spring.addListener(springListener);
            spring.setCurrentValue(0.85f);
            spring.setEndValue(1f);
            if (autoDimiss) {
                ThreadManager.postDelayed(ThreadManager.THREAD_UI, dimissRunnable, delayTime);
            }
        } catch (Exception e) {
            //@note 当对话框正要显示时，用户可能已经退出弹出对话框的界面，导致崩溃
            dismiss();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ThreadManager.removeCallbacks(ThreadManager.THREAD_UI, dimissRunnable);
    }

    @Override
    public void dismiss() {
        spring.setAtRest();
        spring.removeAllListeners();
        ThreadManager.removeCallbacks(ThreadManager.THREAD_UI, dimissRunnable);
        super.dismiss();
    }

    private static class CustomParams {
        public Context context;
        public String title;
        public Drawable icon;
        public boolean autoDimiss;
        public long delayTime;
        public View messageView;
        public View.OnClickListener messageClickListener;
        public int theme;

        public CustomParams(Context context, int theme) {
            this.context = context;
            this.theme = theme;
        }

        public void apply(ReboundToast dialog) {
            if (messageView != null) {
                dialog.setMessageView(messageView);
            } else {
                dialog.setMessageText(title);
                dialog.setMessageIcon(icon);
                dialog.setMessageClickListener(messageClickListener);
            }
            dialog.autoDimiss = autoDimiss;
            dialog.delayTime = delayTime;
        }
    }

    public static class Builder {
        private CustomParams P;

        public Builder(Context context) {
            this(context, 0);
        }

        public Builder(Context context, int theme) {
            P = new CustomParams(context, theme);
        }

        public Context getContext() {
            return P.context;
        }

        public Builder setMessageText(String text) {
            P.title = text;
            return this;
        }

        public Builder setMessageIcon(Drawable icon) {
            P.icon = icon;
            return this;
        }

        public Builder setMessageView(View view) {
            P.messageView = view;
            return this;
        }

        public Builder setAutoDismiss(boolean autoDimiss) {
            P.autoDimiss = autoDimiss;
            return this;
        }

        public Builder setDelayTime(long delayTime) {
            P.delayTime = delayTime;
            return this;
        }

        public Builder setDurationType(@Duration int duration) {
            P.delayTime = duration == Toast.LENGTH_SHORT ?
                    SHORT_DURATION_TIMEOUT : LONG_DURATION_TIMEOUT;
            return this;
        }

        public Builder setMessageClickListener(View.OnClickListener listener) {
            P.messageClickListener = listener;
            return this;
        }

        public ReboundToast create() {
            final ReboundToast dialog = P.theme == 0 ? new ReboundToast(P.context)
                    : new ReboundToast(P.context, P.theme);
            P.apply(dialog);
            return dialog;
        }
    }

    public static ReboundToast show(Context context,
                                    int iconRid,
                                    String message,
                                    boolean autoDimiss,
                                    @Duration int duration,
                                    View.OnClickListener clickListener) {
        ReboundToast toast = new Builder(context)
                .setMessageIcon(ContextCompat.getDrawable(context, iconRid))
                .setMessageText(message)
                .setDurationType(duration)
                .setAutoDismiss(autoDimiss)
                .setMessageClickListener(clickListener)
                .create();
        toast.show();
        return toast;
    }

    public static ReboundToast show(Context context, int iconRid, int messageRid, @Duration int duration) {
        return show(context, iconRid, context.getString(messageRid), true, duration, null);
    }

    public static ReboundToast show(Context context, int iconRid, int messageRid) {
        return show(context, iconRid, context.getString(messageRid), false, Toast.LENGTH_SHORT, null);
    }

    public static ReboundToast show(Context context, int iconRid, int messageRid, View.OnClickListener clickListener) {
        return show(context, iconRid, context.getString(messageRid), false, Toast.LENGTH_SHORT, clickListener);
    }


    public static ReboundToast show(Context context, int iconRid, String message, @Duration int duration) {
        return show(context, iconRid, message, true, duration, null);
    }

    public static ReboundToast show(Context context, int iconRid, String message) {
        return show(context, iconRid, message, false, Toast.LENGTH_SHORT, null);
    }

    public static ReboundToast show(Context context, int iconRid, String message, View.OnClickListener clickListener) {
        return show(context, iconRid, message, false, Toast.LENGTH_SHORT, clickListener);
    }
}
