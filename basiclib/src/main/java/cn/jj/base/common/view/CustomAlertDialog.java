package cn.jj.base.common.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import cn.jj.base.utils.ScreenUtil;
import cn.jj.base.utils.Utility;
import cn.jj.basiclib.R;

import static android.view.View.GONE;

/**
 * 公用的基本Dialog，便于统一样式
 * Created by yangxl on 2016/8/19.
 */
public class CustomAlertDialog extends AlertDialog implements DialogInterface {

    //对话框title布局参数
    private final static int DIALOG_TITLE_VIEW_PADDING_BOTTOM = ScreenUtil.getDimensionPixelSize(R.dimen.dialog_title_padding_bottom);
    private final static int DIALOG_MESSAGE_VIEW_PADDING = ScreenUtil.getDimensionPixelSize(R.dimen.dialog_message_padding_bottom);
    private final static int DIALOG_BUTTON_TOP_PADDING = ScreenUtil.getDimensionPixelSize(R.dimen.dialog_button_top_padding);
    private static int DIALOG_TITLE_MARGIN_LEFT = 24;
    private static int DIALOG_TITLE_TOTAL_HEIGTH = 50;
    private static int DIALOG_TITLE_SIZE = 16;
    private static int DIALOG_TITLE_COLOR = 0xFF333333;

    private static int DIALOG_TITLE_SEPRATOR_COLOR = 0x33000000;
    private static int DIALOG_TITLE_SEPRATOR_SIZE = 1;

    private View mContentView;
    private View mRootView;
    //title布局
    private FrameLayout mTitleContainer;
    private TextView mTitleText;
    private ImageView mTitleIconView;
    private ImageView mTitleDescIconView;

    private TextView mPositiveButton;
    private TextView mNegativeButton;
    /**
     * 对话框内容布局
     */
    private FrameLayout mMessageContainer;
    private LinearLayout mMessageLayout;
    /**
     * 通用的对话框内容容器
     */
    private TextView mMessageText;
    private TextView mSubMessageText;
    private AppCompatCheckBox mNoPrompCheckBox;
    private DimissPermitter dimissPermitter;

    private LinearLayout mBtnContainer;
    private View mBtnContainerDivider;
    private View btnCloseView;

    public CustomAlertDialog(Context context) {
        this(context, R.style.custom_alter_dialog_style);
    }

    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.custom_alert_dialog_layout, null);
        mRootView = view.findViewById(R.id.root);
        mTitleContainer = view.findViewById(R.id.title_container);
        mTitleText = view.findViewById(R.id.title_text);
        mTitleIconView = view.findViewById(R.id.title_icon);
        mTitleDescIconView = view.findViewById(R.id.title_desc_icon);

        mPositiveButton = view.findViewById(R.id.btn_positive);
        mNegativeButton = view.findViewById(R.id.btn_negative);

        mMessageContainer = view.findViewById(R.id.content_container);
        mMessageLayout = view.findViewById(R.id.message_layout);
        mMessageText = view.findViewById(R.id.content);
        mSubMessageText = view.findViewById(R.id.sub_content);
        mNoPrompCheckBox = view.findViewById(R.id.no_promp_checkbox);

        mBtnContainer = view.findViewById(R.id.btn_container);
        mBtnContainerDivider = view.findViewById(R.id.btn_container_divider);
        btnCloseView = view.findViewById(R.id.close_btn);
        setView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(LayoutInflater.from(getContext()).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        mContentView = view;
        FrameLayout rootView = new FrameLayout(getContext());

        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        rootView.addView(mContentView);
        super.setContentView(rootView);
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void dismiss() {
        dismiss(null, 0);
    }

    public void dismiss(OnClickListener buttonListener, int button_identifier) {
        if (buttonListener != null) {
            buttonListener.onClick(this, button_identifier);
        }
        if (dimissPermitter != null && !dimissPermitter.canDimiss(button_identifier)) {
            return;
        }
        super.dismiss();
    }

    @Override
    public void setOnDismissListener(final OnDismissListener listener) {
        super.setOnDismissListener(arg0 -> {
            if (listener != null) {
                listener.onDismiss(arg0);
            }
        });
    }

    private void setDimissPermitter(DimissPermitter dimissPermitter) {
        this.dimissPermitter = dimissPermitter;
    }

    public void setCustomTitle(View titleView) {
        mTitleContainer.removeAllViews();
        mTitleContainer.addView(titleView);
        mTitleContainer.setVisibility(View.VISIBLE);
        //@note 自定义Title区需设置titleContainer底部边距
        mTitleContainer.setPadding(0, 0, 0, DIALOG_TITLE_VIEW_PADDING_BOTTOM);
    }

    public void setMessage(CharSequence message) {
        mMessageContainer.setVisibility(View.VISIBLE);
        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.setText(message);
    }

    private void setMessageStyle(@Nullable Typeface tf) {
        mMessageText.setTypeface(tf);
    }

    private void setMessageMaxLines(@Size(min = 1) int maxLines) {
        if (maxLines == 1) {
            mMessageText.setSingleLine();
        } else if (maxLines > 1) {
            mMessageText.setSingleLine(false);
            mMessageText.setMaxLines(maxLines);
        }
    }

    private void setSubMessage(CharSequence message) {
        mMessageContainer.setVisibility(View.VISIBLE);
        mSubMessageText.setVisibility(View.VISIBLE);
        mSubMessageText.setText(message);
    }

    private void setSubMessageMaxLines(@Size(min = 1) int maxLines) {
        if (maxLines == 1) {
            mSubMessageText.setSingleLine();
        } else if (maxLines > 1) {
            mSubMessageText.setSingleLine(false);
            mSubMessageText.setMaxLines(maxLines);
        }
    }
    private void setNoPrompCheckBox(boolean isShown,
                                    CompoundButton.OnCheckedChangeListener changedLister) {
        if (isShown) {
            mMessageContainer.setVisibility(View.VISIBLE);
            mNoPrompCheckBox.setVisibility(View.VISIBLE);
            mNoPrompCheckBox.setChecked(false);
            mNoPrompCheckBox.setOnCheckedChangeListener(changedLister);
        }
    }

    private void setContainerBackground(Drawable drawable) {
        mRootView.setBackground(drawable);
    }

    public void setIcon(int iconId) {
        mTitleContainer.setVisibility(View.VISIBLE);
        mTitleIconView.setVisibility(View.VISIBLE);
        mTitleIconView.setImageResource(iconId);
    }

    public void setIcon(Drawable icon) {
        mTitleContainer.setVisibility(View.VISIBLE);
        mTitleIconView.setVisibility(View.VISIBLE);
        mTitleIconView.setImageDrawable(icon);
    }

    public void setDescIcon(int iconId) {
        mTitleContainer.setVisibility(View.VISIBLE);
        mTitleDescIconView.setVisibility(View.VISIBLE);
        mTitleDescIconView.setImageResource(iconId);
    }

    public void setDescIcon(Drawable icon) {
        mTitleContainer.setVisibility(View.VISIBLE);
        mTitleDescIconView.setVisibility(View.VISIBLE);
        mTitleDescIconView.setImageDrawable(icon);
    }

    public void setGoneButton(final int button_identifier) {
        switch (button_identifier) {
            case DialogInterface.BUTTON_POSITIVE:
                mPositiveButton.setVisibility(GONE);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mNegativeButton.setVisibility(GONE);
                break;
        }
    }

    private void setBtnVisible(boolean shown) {
        mBtnContainer.setVisibility(shown ? View.VISIBLE : GONE);
    }

    private void setCloseBtnVisible(boolean shown) {
        btnCloseView.setVisibility(shown ? View.VISIBLE : GONE);
        if (shown) {
            btnCloseView.setOnClickListener(v -> dismiss());
        }
    }

    public void setButton(final int button_identifier, CharSequence buttonText,
                          final OnClickListener buttonListener) {
        switch (button_identifier) {
            case DialogInterface.BUTTON_POSITIVE:
                mPositiveButton.setText(buttonText);
                mPositiveButton.setOnClickListener(
                        v -> CustomAlertDialog.this.dismiss(buttonListener, button_identifier));
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mNegativeButton.setText(buttonText);
                mNegativeButton.setOnClickListener(
                        v -> CustomAlertDialog.this.dismiss(buttonListener, button_identifier));
                break;
            default:
                break;
        }
    }

    private void setButtonPadding(int topPadding) {
        mPositiveButton.setPadding(0, topPadding, 0, topPadding);
        mNegativeButton.setPadding(0, topPadding, 0, topPadding);
    }

    private void setNegativeButtonTextColor(@ColorInt int color) {
        mNegativeButton.setTextColor(color);
    }

    @Override
    public void show() {
        try {
//            setStatusBarFullTransparent();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            super.show();
            ViewGroup.LayoutParams lp= getWindow().getDecorView().getLayoutParams();
            lp.width = Math.min(ScreenUtil.getScreenHeight(),ScreenUtil.getScreenWidth());
            getWindow().getDecorView().setLayoutParams(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } catch (Exception e) {
            /*todo  全局对话框中，使用type==TYPE_SYSTEM_ALERT ||TYPE_SYSTEM_ERROR 时，部分手机可能会有异常：
             Unable to add window android.view.ViewRootImpl$W@e67b1fc -- permission denied for this window type
             暂时处理逻辑：崩溃时，默认走确定按钮点击效果
            */
            if (mPositiveButton.getVisibility() == View.VISIBLE) {
                mPositiveButton.callOnClick();
            } else {
                dismiss();
            }
        }
    }

    /**
     * 默认隐藏navigation_bar（虚拟按键），透明状态栏，可定义亮状态栏或者隐藏状态栏
     */
    private void setStatusBarFullTransparent() {
        if (getWindow() == null) {
            return;
        }
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            setDecorViewFlag();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setDecorViewFlag();
        } else {
            setDecorViewFlag();
        }
    }

    private static int flagL =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private static int flagLFull =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private static int flagK =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private static int flagKFull =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private boolean lightStatusBar;

    private boolean hideStatusBar;

    protected void setLightStatusBar(boolean lightStatusBar) {
        this.lightStatusBar = lightStatusBar;
    }

    protected void setHideStatusBar(boolean hideStatusBar) {
        this.hideStatusBar = hideStatusBar;
    }

    private void setDecorViewFlag() {
        if (getWindow() == null) {
            return;
        }
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= 23) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagLFull);
            } else {
                decorView.setSystemUiVisibility(lightStatusBar ? flagL | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : flagL);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagLFull);
            } else {
                decorView.setSystemUiVisibility(flagL);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hideStatusBar) {
                decorView.setSystemUiVisibility(flagKFull);
            } else {
                decorView.setSystemUiVisibility(flagK);
            }
        } else {
            decorView.setSystemUiVisibility(View.GONE);
        }
    }

    public void setMessageView(View view) {
        mMessageContainer.removeAllViews();
        mMessageContainer.setVisibility(View.VISIBLE);
        mBtnContainerDivider.setVisibility(GONE);
        mMessageContainer.addView(view);
    }

    private void setMessageContainerBottomPadding(int bottomPadding) {
        //@note 自定义Message区统一设置messageContainer底部边距
        mMessageContainer.setPadding(0, 0, 0, bottomPadding);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title) &&
                mTitleIconView.getVisibility() != View.VISIBLE &&
                mTitleDescIconView.getVisibility() != View.VISIBLE) {
            mTitleContainer.setVisibility(GONE);
            //@note title区不可见时，则要设置messageLayout与dialog上边距
//            mMessageLayout.setPadding(0, DIALOG_MESSAGE_VIEW_PADDING, 0, 0);
            return;
        }
        mTitleContainer.setVisibility(View.VISIBLE);
        mTitleText.setText(title);
    }

    /**
     * 创建一个默认的TitleView
     */
    private static View createDefaultTitleView(Context context, String title) {

        FrameLayout titleLayout = new FrameLayout(context);
        titleLayout.setMinimumHeight(ScreenUtil.dp2px(DIALOG_TITLE_TOTAL_HEIGTH));

        TextView textView = new TextView(context);
        FrameLayout.LayoutParams textParms =
                new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParms.leftMargin = ScreenUtil.dp2px(DIALOG_TITLE_MARGIN_LEFT);
        textParms.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        textView.setLayoutParams(textParms);
        textView.setTextColor(DIALOG_TITLE_COLOR);
        textView.setTextSize(DIALOG_TITLE_SIZE);
        textView.setText(title);

        View line = new View(context);
        FrameLayout.LayoutParams lineParms =
                new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DIALOG_TITLE_SEPRATOR_SIZE);
        lineParms.gravity = Gravity.BOTTOM;
        line.setLayoutParams(lineParms);
        line.setBackgroundColor(DIALOG_TITLE_SEPRATOR_COLOR);

        titleLayout.addView(textView);
        titleLayout.addView(line);
        titleLayout.setVisibility(View.VISIBLE);
        return titleLayout;
    }

    private static class CustomParams {
        public Context mContext;
        public int theme;
        public CharSequence title;
        public View customTitleView;
        public CharSequence message;
        public CharSequence subMessage;
        public int iconId;
        public Drawable icon;
        private int descIconId;
        private Drawable descIcon;
        public CharSequence positiveButtonText;
        public OnClickListener positiveButtonListener;

        public CharSequence negativeButtonText;
        public OnClickListener negativeButtonListener;
        private @ColorInt
        int negativeBtnTextColor =
                ContextCompat.getColor(Utility.getApplication(), R.color.basic_dialog_button_text);
        private int buttonTopPadding = DIALOG_BUTTON_TOP_PADDING;

        public boolean mCancelable = true;
        public boolean mCanceledOnTouchOutside = true;
        public OnCancelListener mOnCancelListener;
        public OnDismissListener mOnDismissListener;
        public OnKeyListener mOnKeyListener;
        public View mMessageView;
        public DimissPermitter dimissPermitter;

        public boolean isNoPrompCheckBoxShown;
        public CompoundButton.OnCheckedChangeListener noPrompChangedLister;

        private int messageMaxLines = 2;
        private int subMessageMaxLines = 2;
        private Typeface msgTf = null;
        private Drawable containerBG = null;

        /**
         * 是否为全局对话框，全局对话框主要给后台服务使用
         */
        public boolean isGlobalDialog;
        private boolean isSetMessageContainerBottomPadding = true;

        public boolean showClose;

        private boolean lightStatusBar;

        private boolean hideStatusBar;

        public CustomParams(Context context, int theme) {
            this.mContext = context;
            this.theme = theme;
        }

        public void apply(CustomAlertDialog dialog) {
            if (null != containerBG) {
                dialog.setContainerBackground(containerBG);
            }
            if (customTitleView != null) {
                dialog.setCustomTitle(customTitleView);
            } else {
                dialog.setTitle(title);
                if (iconId > 0) {
                    dialog.setIcon(iconId);
                } else if (icon != null) {
                    dialog.setIcon(icon);
                }
                if (descIconId > 0) {
                    dialog.setDescIcon(descIconId);
                } else if (descIcon != null) {
                    dialog.setDescIcon(descIcon);
                }
            }
            if (mMessageView != null) {
                dialog.setMessageView(mMessageView);
//                if (isSetMessageContainerBottomPadding) {
//                    dialog.setMessageContainerBottomPadding(DIALOG_MESSAGE_VIEW_PADDING);
//                }
            } else {
                if (message != null) {
                    dialog.setMessage(message);
                    dialog.setMessageStyle(msgTf);
                    dialog.setMessageMaxLines(messageMaxLines);
                }
                if (subMessage != null) {
                    dialog.setSubMessage(subMessage);
                    dialog.setSubMessageMaxLines(subMessageMaxLines);
                } else {
                    if (!isNoPrompCheckBoxShown) {
                        dialog.setMessageContainerBottomPadding(DIALOG_TITLE_VIEW_PADDING_BOTTOM);
                    }
                }
            }

            boolean isBtnContainerShown = false;
            if (positiveButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        positiveButtonText, positiveButtonListener);
                isBtnContainerShown = true;
            } else {
                dialog.setGoneButton(DialogInterface.BUTTON_POSITIVE);
            }

            if (negativeButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        negativeButtonText, negativeButtonListener);
                isBtnContainerShown = true;
                dialog.setNegativeButtonTextColor(negativeBtnTextColor);
            } else {
                dialog.setGoneButton(DialogInterface.BUTTON_NEGATIVE);
            }
            if (isBtnContainerShown) {
                dialog.setButtonPadding(buttonTopPadding);
            }
            dialog.setBtnVisible(isBtnContainerShown);

            dialog.setNoPrompCheckBox(isNoPrompCheckBoxShown, noPrompChangedLister);
            dialog.setDimissPermitter(dimissPermitter);

            dialog.setCloseBtnVisible(showClose);
            if (isGlobalDialog) {
                if (dialog.getWindow() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    } else {
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    }
                }
            }

            dialog.setLightStatusBar(lightStatusBar);
            dialog.setHideStatusBar(hideStatusBar);
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
            return P.mContext;
        }

        public Builder setTitle(int titleId) {
            try {
                P.title = P.mContext.getString(titleId);
            } catch (Exception e) {
            }
            return this;
        }

        public Builder setTitle(CharSequence title) {
            P.title = title;
            return this;
        }

        public Builder setCustomTitle(View customTitleView) {
            P.customTitleView = customTitleView;
            return this;
        }

        //定义一个默认的custom title样式
        public Builder setCustomTitle(Context context, String title) {
            P.customTitleView = createDefaultTitleView(context, title);
            return this;
        }

        public Builder setMessage(int messageId) {
            try {
                P.message = P.mContext.getString(messageId);
            } catch (Exception e) {
            }
            return this;
        }

        public Builder setMessage(CharSequence message) {
            P.message = message;
            return this;
        }

        public Builder setMessageMaxLines(@Size(min = 1) int maxLines) {
            P.messageMaxLines = maxLines;
            return this;
        }

        public Builder setSubMessageMaxLines(@Size(min = 1) int maxLines) {
            P.subMessageMaxLines = maxLines;
            return this;
        }

        public Builder setMessageStyle(@Nullable Typeface tf) {
            P.msgTf = tf;
            return this;
        }

        public Builder setSubMessage(int messageId) {
            try {
                P.subMessage = P.mContext.getString(messageId);
            } catch (Exception e) {
            }
            return this;
        }

        public Builder setSubMessage(CharSequence subMessage) {
            P.subMessage = subMessage;
            return this;
        }

        public Builder setIcon(int iconId) {
            P.iconId = iconId;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            P.icon = icon;
            return this;
        }


        public Builder showClose(boolean showClose) {
            P.showClose = showClose;
            return this;
        }


        public Builder setDescIcon(int iconId) {
            P.descIconId = iconId;
            return this;
        }

        public Builder setDescIcon(Drawable icon) {
            P.descIcon = icon;
            return this;
        }

        public Builder setPositiveButton(int textId,
                                         final OnClickListener listener) {
            P.positiveButtonText = P.mContext.getString(textId);
            P.positiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text,
                                         final OnClickListener listener) {
            P.positiveButtonText = text;
            P.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId,
                                         final OnClickListener listener) {
            P.negativeButtonText = P.mContext.getText(textId);
            P.negativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text,
                                         final OnClickListener listener) {
            P.negativeButtonText = text;
            P.negativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButtonTextColor(@ColorInt int color) {
            P.negativeBtnTextColor = color;
            return this;
        }

        public Builder setButtonPadding(int topPadding) {
            P.buttonTopPadding = topPadding;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            P.mCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setDimissPermitter(DimissPermitter dimissPermitter) {
            P.dimissPermitter = dimissPermitter;
            return this;
        }

        /**
         * 调用该方法后,默认的message对应的TextView和CheckBox都会被删除,只有指定的view占着message区域.
         *
         * @param view
         * @return
         */
        public Builder setMessageView(View view) {
            P.mMessageView = view;
            return this;
        }

        /**
         * 调用该方法后,默认的message对应的TextView和CheckBox都会被删除,只有指定的view占着message区域.
         *
         * @param layoutResID
         * @return
         */
        public Builder setMessageView(int layoutResID) {
            P.mMessageView = LayoutInflater.from(getContext()).inflate(layoutResID, null);
            return this;
        }

        public Builder setGlobalDialog(boolean isGlobalDialog) {
            P.isGlobalDialog = isGlobalDialog;
            return this;
        }

        public Builder setMessageContainerBottomPaddingEnable(boolean isSetMessageContainerBottomPadding) {
            P.isSetMessageContainerBottomPadding = isSetMessageContainerBottomPadding;
            return this;
        }

        public Builder setLightStatusBar(boolean lightStatusBar) {
            P.lightStatusBar = lightStatusBar;
            return this;
        }

        public Builder setHideStatusBar(boolean hideStatusBar) {
            P.hideStatusBar = hideStatusBar;
            return this;
        }

        public Builder setNoPrompCheckBoxShown(
                boolean isShown, CompoundButton.OnCheckedChangeListener changedLister) {
            P.isNoPrompCheckBoxShown = isShown;
            P.noPrompChangedLister = changedLister;
            return this;
        }

        public Builder setContainerBackGround(Drawable drawable) {
            P.containerBG = drawable;
            return this;
        }

        public Builder setContainerBackGround(@DrawableRes int resId) {
            P.containerBG = ContextCompat.getDrawable(Utility.getApplication(), resId);
            return this;
        }

        public CustomAlertDialog create() {
            final CustomAlertDialog dialog = P.theme == 0 ? new CustomAlertDialog(P.mContext)
                    : new CustomAlertDialog(P.mContext, P.theme);
            P.apply(dialog);
            dialog.setCancelable(P.mCancelable);
            dialog.setCanceledOnTouchOutside(P.mCanceledOnTouchOutside);
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (!P.mCancelable) {
                dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
                        return true;
                    }
                    boolean result = false;
                    if (P.mOnKeyListener != null) {
                        result = P.mOnKeyListener.onKey(dialog1, keyCode, event);
                    }
                    dialog1.dismiss();
                    return result;
                });
            } else {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        public CustomAlertDialog show() {
            CustomAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    public interface DimissPermitter {
        boolean canDimiss(int which);
    }

    public void setCheckStatus(boolean isChecked) {
        mNoPrompCheckBox.setChecked(isChecked);
    }
}
