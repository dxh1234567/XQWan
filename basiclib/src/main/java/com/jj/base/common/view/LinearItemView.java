package com.jj.base.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.jj.base.utils.BitmapUtil;
import com.jj.base.utils.FontUtil;
import com.jj.base.utils.ScreenUtil;
import com.jj.base.utils.ViewUtil;
import com.jj.basiclib.R;


/**
 * Created by yangxl on 2016/8/16.
 * |--------------------------------------------------------------|----------|
 * | |--------| |            Title             |                  |          |
 * | |Drawable|              Desc                     |  Value  | |CustomView|
 * | |--------| |            Desc2             |                  |          |
 * |--------------------------------------------------------------|----------|
 */

public class LinearItemView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "LinearItemView";
    private static final int DEFAULT_TITLE_TEXT_SIZE = ScreenUtil.sp2px(16);  //SP
    private static final int DEFAULT_TITLE_TEXT_COLOR = 0xff333333;
    private static final int DEFAULT_DESC_TEXT_SIZE = ScreenUtil.sp2px(10);   //SP
    private static final int DEFAULT_DESC_TEXT_COLOR = 0xff999999;
    private static final int DEFAULT_VALUE_TEXT_SIZE = ScreenUtil.sp2px(10);   //SP
    private static final int DEFAULT_VALUE_TEXT_COLOR = 0xff999999;

    private static final int MIN_HEIGHT = ScreenUtil.dp2px(40);
    //title与desc之间的行距
    private static final int TITLE_TO_DESC_LINE_PADDING = ScreenUtil.dp2px(10);
    private static final int DESC_TO_DESC_LINE_PADDING = ScreenUtil.dp2px(8);
    private static final int TITLE_TO_ICON_PADDING = ScreenUtil.dp2px(10);
    private static final int VALUE_TO_CUSTOMVIEW_PADDING = ScreenUtil.dp2px(10);
    private static final int DESC_TO_VALUE_MIN_PADDING = ScreenUtil.dp2px(10);

    protected static final int DEFAULT_PADDING_TOP = ScreenUtil.dp2px(10);
    protected static final int DEFAULT_PADDING_BOTTOM = ScreenUtil.dp2px(10);
    protected static final int DEFAULT_PADDING_LEFT = ScreenUtil.dp2px(20);
    protected static final int DEFAULT_PADDING_RIGHT = ScreenUtil.dp2px(20);

    private static final int DEFAULT_ICON_WIDTH = ScreenUtil.dp2px(50);
    private static final int DEFAULT_ICON_HEIGHT = ScreenUtil.dp2px(50);

    private static final int DEFAULT_DIVER_LINE_HEIGHT = ScreenUtil.dp2px(0.5f);
    private static final int DEFAULT_DIVER_LINE_T_COLOR = 0xffdcdcdc;

    /**
     * 包含的customview种类
     */
    public static final int CUSTOMVIEW_NONE = 0;
    public static final int CUSTOMVIEW_CHECKBOX = 1;
    public static final int CUSTOMVIEW_SWITCHER = 2;
    public static final int CUSTOMVIEW_MORE = 3;
    public static final int CUSTOMVIEW_BUTTON = 4;
    public static final int CUSTOMVIEW_RADIO = 5;
    public static final int CUSTOMVIEW_LABEL = 6;
    /**
     * mIconTagDrawable 相对mDrawable的位置
     */
    public static final int TAGICON_TOP_RIGHT = 1;
    public static final int TAGICON_TOP_LEFT = 2;
    public static final int TAGICON_BOTTOM_RIGHT = 3;
    public static final int TAGICON_BOTTOM_LEFT = 4;

    /**
     * label 和desc区域可绘制的最长宽度
     */
    protected float mTitleDescMaxWidth;

    protected String mTitle;
    private SimpleSpannableString mSpannableTitle;  //简单的富文本title
    protected int mTitleColor;
    protected float mTitleSize;
    protected float mTitleOffsetX;
    protected float mTitleOffsetY;
    protected int mTitleTextStyleIndex;

    /**
     * 对item的描述最多有两行
     * 注意：第一行与第二行的字体一致
     */
    protected String mDesc;
    protected String mDesc2;   //描述的第二行
    protected int mDescColor;
    protected int mDesc2Color;
    protected float mDescSize;
    protected float mDescOffsetX;
    protected float mDescOffsetY;
    protected float mDescOffsetY2;
    protected int mDescTextStyleIndex;

    protected String mValue;
    protected float mValueSize;
    protected int mValueColor;
    protected float mValueOffsetX;
    protected float mValueOffsetY;
    protected int mValueTextStyleIndex;
    private Paint.Align valueAlignTo = Paint.Align.RIGHT; //value对齐方式

    protected Drawable mDrawable;
    protected Rect mIconBoundRect;
    private int mIconWidth;
    private int mIconHeight;
    private int mIconTint;

    protected Drawable mIconTagDrawable;
    protected Rect mTagIconBoundRect;
    protected int mTagPositionType;
    private int mTagIconWidth;
    private int mTagIconHeight;


    protected TextPaint mTitlePaint;
    protected TextPaint mDescPaint;
    protected TextPaint mValuePaint;

    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mPaddingTop;
    protected int mPaddingBottom;

    protected float mTitleToDescDistanceY;
    protected float mDescToDescDistanceY;
    protected float mTitleToIconDistanceX;
    /**
     * 自定义view
     */
    private View mCustomView;
    private int mCustomViewType;

    private OnLinearItemClickListener mOnLinearItemClickListener;

    /**
     * 底部分割线
     */
    public static final int DIVER_ALIGN_TOP = 0x1;
    public static final int DIVER_ALIGN_BOTTOM = 0x1 << 1;
    private int mDiverLineShowType = DIVER_ALIGN_BOTTOM;
    private float mDiverLineHeight;
    private int mDiverLineColor;
    private Drawable mDiverDrawable;
    protected Rect mTopDiverBoundRect;
    protected Rect mBottomDiverBoundRect;
    /**
     * 控制各区域的显示
     */
    private boolean mIsTitleVisible = true;
    /**
     * 强制desc区域显示，即使mdesc为空，这样保证title不会垂直居中
     */
    private boolean mIsDescVisible = false;
    private boolean mIsValueVisible = false;
    private boolean mIsIconVisible = false;
    private boolean mIsCustomViewVisible = false;
    private boolean mIsShowDiverLine = false;

    public void setTitleVisible(boolean isVisible) {
        mIsTitleVisible = isVisible;
    }

    public void setDescVisible(boolean isVisible) {
        mIsDescVisible = isVisible;
    }

    public void setValueVisible(boolean isVisible) {
        mIsValueVisible = isVisible;
    }

    public void setIconVisible(boolean isVisible) {
        mIsIconVisible = isVisible;
    }

    public void setCustomViewVisible(boolean isVisible) {
        mIsCustomViewVisible = isVisible;
    }

    /**
     * 绘制所需参数是否已经计算过了
     */
    protected boolean mIsComputed;
    /**
     * 控件高度
     */
    protected int mTotalHeight;

    public LinearItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setWillNotDraw(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化自定义变量
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinearItemView);
        mTitle = a.getString(R.styleable.LinearItemView_LIV_titleText);
        //保证mTitle不为null
        mTitle = mTitle == null ? "" : mTitle;

        mTitleSize = a.getDimension(R.styleable.LinearItemView_LIV_titleTextSize, DEFAULT_TITLE_TEXT_SIZE);
        mTitleColor = a.getColor(R.styleable.LinearItemView_LIV_titleTextColor, DEFAULT_TITLE_TEXT_COLOR);
        mTitleTextStyleIndex = a.getInt(R.styleable.LinearItemView_LIV_titleTextStyle, -1);

        mDesc = a.getString(R.styleable.LinearItemView_LIV_descText);
        mDesc2 = a.getString(R.styleable.LinearItemView_LIV_descText2);
        mDescSize = a.getDimension(R.styleable.LinearItemView_LIV_descTextSize, DEFAULT_DESC_TEXT_SIZE);
        mDescColor = a.getColor(R.styleable.LinearItemView_LIV_descTextColor, DEFAULT_DESC_TEXT_COLOR);
        mDesc2Color = a.getColor(R.styleable.LinearItemView_LIV_desc2TextColor, DEFAULT_DESC_TEXT_COLOR);
        mDescTextStyleIndex = a.getInt(R.styleable.LinearItemView_LIV_descTextStyle, -1);

        mValue = a.getString(R.styleable.LinearItemView_LIV_valueText);
        mValueSize = a.getDimension(R.styleable.LinearItemView_LIV_valueTextSize, DEFAULT_VALUE_TEXT_SIZE);
        mValueColor = a.getColor(R.styleable.LinearItemView_LIV_valueTextColor, DEFAULT_VALUE_TEXT_COLOR);
        mValueTextStyleIndex = a.getInt(R.styleable.LinearItemView_LIV_valueTextStyle, -1);

        //diver drawable
        mDiverDrawable = a.getDrawable(R.styleable.LinearItemView_LIV_diverLine);
        mDiverLineShowType = a.getInt(R.styleable.LinearItemView_LIV_diverLineShowType, DIVER_ALIGN_BOTTOM);

        mPaddingLeft = (int) a.getDimension(R.styleable.LinearItemView_LIV_itemPaddingLeft, DEFAULT_PADDING_LEFT);
        mPaddingRight = (int) a.getDimension(R.styleable.LinearItemView_LIV_itemPaddingRight, DEFAULT_PADDING_RIGHT);
        mPaddingTop = (int) a.getDimension(R.styleable.LinearItemView_LIV_itemPaddingTop, DEFAULT_PADDING_TOP);
        mPaddingBottom = (int) a.getDimension(R.styleable.LinearItemView_LIV_itemPaddingBottom, DEFAULT_PADDING_BOTTOM);

        //icon
        mDrawable = a.getDrawable(R.styleable.LinearItemView_LIV_itemIcon);
        mIconWidth = (int) a.getDimension(R.styleable.LinearItemView_LIV_iconWidth, DEFAULT_ICON_WIDTH);
        mIconHeight = (int) a.getDimension(R.styleable.LinearItemView_LIV_iconHeight, DEFAULT_ICON_HEIGHT);
        mIconTint = a.getColor(R.styleable.LinearItemView_LIV_itemIconTint, -1);

        //tag icon
        mIconTagDrawable = a.getDrawable(R.styleable.LinearItemView_LIV_itemTagIcon);
        mTagIconWidth = (int) a.getDimension(R.styleable.LinearItemView_LIV_tagIconWidth, DEFAULT_ICON_WIDTH);
        mTagIconHeight = (int) a.getDimension(R.styleable.LinearItemView_LIV_tagIconHeight, DEFAULT_ICON_HEIGHT);
        mTagPositionType = a.getInt(R.styleable.LinearItemView_LIV_tagIconPositionType, TAGICON_TOP_RIGHT);

        mCustomViewType = a.getInt(R.styleable.LinearItemView_LIV_itemCustomView, CUSTOMVIEW_NONE);

        mTitleToDescDistanceY = (int) a.getDimension(R.styleable.LinearItemView_LIV_titleToDescDistance, TITLE_TO_DESC_LINE_PADDING);
        mDescToDescDistanceY = (int) a.getDimension(R.styleable.LinearItemView_LIV_descToDescDistance, DESC_TO_DESC_LINE_PADDING);
        mTitleToIconDistanceX = (int) a.getDimension(R.styleable.LinearItemView_LIV_titleToIconDistance, TITLE_TO_ICON_PADDING);

        mDiverLineHeight = a.getDimension(R.styleable.LinearItemView_LIV_diverLineHeight, DEFAULT_DIVER_LINE_HEIGHT);
        mDiverLineColor = a.getColor(R.styleable.LinearItemView_LIV_diverLineColor, DEFAULT_DIVER_LINE_T_COLOR);
        mIsShowDiverLine = a.hasValue(R.styleable.LinearItemView_LIV_diverLine) ||
                a.hasValue(R.styleable.LinearItemView_LIV_diverLineColor) ||
                a.hasValue(R.styleable.LinearItemView_LIV_diverLineHeight);
        a.recycle();

        mTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextAlign(Paint.Align.LEFT);
        mTitlePaint.setTextSize(mTitleSize);
        setTypeface(mTitleTextStyleIndex, mTitlePaint);
//        mTitlePaint.setTypeface();

        mDescPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mDescPaint.setColor(mDescColor);
        mDescPaint.setTextAlign(Paint.Align.LEFT);
        mDescPaint.setTextSize(mDescSize);
        setTypeface(mDescTextStyleIndex, mDescPaint);

//        mDescPaint.setTypeface();

        mValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setTextAlign(valueAlignTo);
        mValuePaint.setTextSize(mValueSize);
        setTypeface(mValueTextStyleIndex, mValuePaint);


//        mValuePaint.setTypeface();

        //初始化customView
        initCustomView();

//        setOnClickListener(this);
    }

    protected void setTypeface(int style, Paint textPaint) {
        if (style > 0) {
            Typeface tf = textPaint.getTypeface();
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = style & ~typefaceStyle;
            textPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            textPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            textPaint.setFakeBoldText(false);
            textPaint.setTextSkewX(0);
        }
    }

    private void initCustomView() {
        switch (mCustomViewType) {
            case CUSTOMVIEW_SWITCHER: {
                Context context = getContext();
                SwitchCompat switcher = new SwitchCompat(context);
                switcher.setThumbTintList(ContextCompat.getColorStateList(context, R.color.selector_switcher_thumb));
                switcher.setTrackTintList(ContextCompat.getColorStateList(context, R.color.selector_switcher_track));
                switcher.setClickable(false);
                switcher.setEnabled(false);
                addCustomView(switcher, null);
                break;
            }
            case CUSTOMVIEW_CHECKBOX: {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setButtonDrawable(R.drawable.checkbox_bg);
                checkBox.setClickable(false);
                checkBox.setEnabled(false);
                addCustomView(checkBox, null);
                break;
            }
            case CUSTOMVIEW_MORE: {
                ImageView imgView = new ImageView(getContext());
                imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imgView.setImageResource(R.drawable.ic_more_arrow);
                addCustomView(imgView, null);
                break;
            }
            case CUSTOMVIEW_BUTTON: {
                final TextView button = new TextView(getContext());
                button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().
                        getDimensionPixelSize(R.dimen.btn_common_font_size));
                button.setBackgroundResource(R.drawable.btn_common_round_rect_open_bg);
                button.setTextColor(getResources().getColor(R.color.basic_liner_item_view_btn_text));
                button.setSingleLine();
                button.setGravity(Gravity.CENTER);
                addCustomView(button, null);
                break;
            }
            case CUSTOMVIEW_LABEL: {
                final TextView label = new TextView(getContext());
                label.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().
                        getDimensionPixelSize(R.dimen.btn_common_font_size));
                label.setTextColor(getResources().getColor(R.color.basic_liner_item_view_btn_text));
                label.setSingleLine();
                label.setGravity(Gravity.CENTER);
                addCustomView(label, null);
                break;
            }
            case CUSTOMVIEW_RADIO: {
                final RadioButton radioButton = new RadioButton(getContext());
                addCustomView(radioButton, null);
                break;
            }
            case CUSTOMVIEW_NONE: {
                if (mCustomView != null) {
                    removeView(mCustomView);
                    mCustomView = null;
                    requestLayout();
                }
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,
                Math.max(getSuggestedMinimumHeight(), resolveSize(MIN_HEIGHT, heightMeasureSpec)));
        if (mCustomView != null && mIsCustomViewFillHight) {
            measureChild(mCustomView, widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        //获取控件高度
        float totalHeight = 0;
        float valueTextHeight = FontUtil.calcTextHeight(mTitlePaint, "8");
        if (!TextUtils.isEmpty(mDesc)) {
            valueTextHeight += FontUtil.calcTextHeight(mDescPaint, "8");
            valueTextHeight += mTitleToDescDistanceY;
        }
        if (!TextUtils.isEmpty(mDesc2)) {
            valueTextHeight += FontUtil.calcTextHeight(mDescPaint, "8");
            valueTextHeight += mDescToDescDistanceY;
        }

        float imgHeight = 0;
        if (mDrawable != null) {
            imgHeight = mIconHeight;
        }
        float customViewHeight = 0;
        if (mCustomView != null) {
            customViewHeight = mCustomView.getHeight();
        }
        totalHeight = Math.max(imgHeight, valueTextHeight);
        totalHeight += mPaddingTop;
        totalHeight += mPaddingBottom;
        totalHeight += (mDiverDrawable != null ? mDiverDrawable.getIntrinsicHeight() : mDiverLineHeight);
        totalHeight = Math.max(totalHeight, customViewHeight);
        totalHeight = Math.max(totalHeight, MIN_HEIGHT);
        mTotalHeight = (int) totalHeight;
        return (int) totalHeight;
    }


    /**
     * 计算各区域的相对位置
     */
    private void calcOffsetXY() {
        if (mIsComputed) {
            return;
        }
        mIsComputed = true;
        int iconWidth = 0;
        mTotalHeight = Math.max(mTotalHeight, getHeight());
        if (mDrawable != null) {
            if (mIconBoundRect == null) {
                mIconBoundRect = new Rect();
            }
            //确定icon的绘制区域
            int top = (mTotalHeight - mIconHeight) / 2;
            mIconBoundRect.set(mPaddingLeft, top,
                    mPaddingLeft + mIconWidth, top + mIconHeight);
            iconWidth = mIconWidth;
            if (mIconTint != -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mDrawable.setTint(mIconTint);
                } else {
                    mDrawable = BitmapUtil.getTintDrawable(mDrawable, mIconTint, null);
                }
            }
        }

        if (mIconTagDrawable != null && mIconBoundRect != null) {
            if (mTagIconBoundRect == null) {
                mTagIconBoundRect = new Rect();
            }
            switch (mTagPositionType) {
                case TAGICON_TOP_LEFT: {
                    mTagIconBoundRect.set(mIconBoundRect.left, mIconBoundRect.top,
                            mIconBoundRect.left + mTagIconWidth, mIconBoundRect.top + mTagIconHeight);
                    break;
                }
                case TAGICON_TOP_RIGHT: {
                    mTagIconBoundRect.set(mIconBoundRect.right - mTagIconWidth, mIconBoundRect.top,
                            mIconBoundRect.right, mIconBoundRect.top + mTagIconHeight);
                    break;
                }
                case TAGICON_BOTTOM_LEFT: {
                    mTagIconBoundRect.set(mIconBoundRect.left, mIconBoundRect.bottom - mTagIconHeight,
                            mIconBoundRect.left + mTagIconWidth, mIconBoundRect.bottom);
                    break;
                }
                case TAGICON_BOTTOM_RIGHT: {
                    mTagIconBoundRect.set(mIconBoundRect.right - mTagIconWidth, mIconBoundRect.bottom - mTagIconHeight,
                            mIconBoundRect.right, mIconBoundRect.bottom);
                    break;
                }
            }
        }
        if (mDiverDrawable != null) {

            if ((mDiverLineShowType & DIVER_ALIGN_TOP) != 0) {
                if (mTopDiverBoundRect == null) {
                    mTopDiverBoundRect = new Rect();
                }
                mDiverDrawable.getPadding(mTopDiverBoundRect);
                mTopDiverBoundRect.set(mTopDiverBoundRect.left,
                        mTopDiverBoundRect.top,
                        getWidth() - mTopDiverBoundRect.right,
                        mDiverDrawable.getIntrinsicHeight());
            }
            if ((mDiverLineShowType & DIVER_ALIGN_BOTTOM) != 0) {
                if (mBottomDiverBoundRect == null) {
                    mBottomDiverBoundRect = new Rect();
                }
                mDiverDrawable.getPadding(mBottomDiverBoundRect);
                mBottomDiverBoundRect.set(mBottomDiverBoundRect.left,
                        mTotalHeight - mDiverDrawable.getIntrinsicHeight(),
                        getWidth() - mBottomDiverBoundRect.right,
                        mTotalHeight);
            }
        }

        float titleTextHeight = FontUtil.calcTextHeight(mTitlePaint, "8");
        float descTextHeight = FontUtil.calcTextHeight(mDescPaint, "8");

        mTitleOffsetX = mPaddingLeft + (iconWidth != 0 ? iconWidth + mTitleToIconDistanceX : 0);
        mDescOffsetX = mTitleOffsetX;

        float rightViewWidth = getWidth() - mPaddingRight;
        if (mCustomView != null) {
            rightViewWidth = mCustomView.getLeft() - VALUE_TO_CUSTOMVIEW_PADDING;
        }
        if (!TextUtils.isEmpty(mValue)) {
            rightViewWidth -= DESC_TO_VALUE_MIN_PADDING;
        }

        int customWidth = 0;
        if (mCustomView != null) {
            customWidth = mCustomView.getWidth();
        }
        if (!TextUtils.isEmpty(mValue)) {
            FontUtil.FontSize valueFontSize = FontUtil.calcTextSize(mValuePaint, mValue);
            if (valueAlignTo == Paint.Align.RIGHT) {
                //右对齐，mValueOffsetX为文字最右边偏移

                mValueOffsetX = getWidth() - customWidth - mPaddingRight;
                mValueOffsetX -= customWidth == 0 ? 0 : VALUE_TO_CUSTOMVIEW_PADDING;
            } else {
                //左对齐，mValueOffsetX为文字最左边偏移
                mValueOffsetX = mTitleOffsetX + FontUtil.calcTextWidth(mTitlePaint, mTitle)
                        + VALUE_TO_CUSTOMVIEW_PADDING;
            }
            mValueOffsetY = (getHeight() + valueFontSize.height) / 2;
            //优先保证value显示完全
            rightViewWidth -= valueFontSize.width;
        }

        mTitleDescMaxWidth = rightViewWidth - mTitleOffsetX;
        if (!TextUtils.isEmpty(mTitle)) {
            mTitle = TextUtils.ellipsize(mTitle, mTitlePaint, mTitleDescMaxWidth, TextUtils.TruncateAt.END).toString();
        }
        if (!TextUtils.isEmpty(mDesc)) {
            mDesc = TextUtils.ellipsize(mDesc, mDescPaint, mTitleDescMaxWidth, TextUtils.TruncateAt.END).toString();
        }
        if (!TextUtils.isEmpty(mDesc2)) {
            mDesc2 = TextUtils.ellipsize(mDesc2, mDescPaint, mTitleDescMaxWidth, TextUtils.TruncateAt.END).toString();
        }
        //todo
        if (TextUtils.isEmpty(mDesc) && !mIsDescVisible) {
            mTitleOffsetY = getHeight() / 2 + titleTextHeight / 2;
        } else {
            float totalFontHeight = titleTextHeight + descTextHeight + mTitleToDescDistanceY;
            if (!TextUtils.isEmpty(mDesc2)) {
                totalFontHeight += descTextHeight + mDescToDescDistanceY;
            }
            float padding = getHeight() - totalFontHeight;
            mTitleOffsetY = padding / 2 + titleTextHeight;
        }
        //不管mDesc是否为空，都应有高度
        mDescOffsetY = mTitleOffsetY + descTextHeight + mTitleToDescDistanceY;
        mDescOffsetY2 = mDescOffsetY + descTextHeight + mDescToDescDistanceY;
        onExtraCalcOffsetXY();
    }

    protected void onExtraCalcOffsetXY() {
    }

    public void setCustomView(View customView, LayoutParams layoutParams) {
        addCustomView(customView, layoutParams);
    }

    public View getCustomView() {
        return mCustomView;
    }

    private boolean mIsCustomViewFillHight = false;

    private void addCustomView(View customView, LayoutParams layoutParams) {
        LayoutParams lp = layoutParams;
        if (customView != null) {
            if (lp == null) {
                lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = mPaddingBottom;
                lp.topMargin = mPaddingTop;
                lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            }
            lp.rightMargin = mPaddingRight;

            if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                mIsCustomViewFillHight = true;
            }
            if (mCustomView != null) {
                removeView(mCustomView);
            }
            mCustomView = customView;
            addView(mCustomView, lp);
            requestLayout();
        }
    }

    public FrameLayout.LayoutParams getCustomViewLayoutParams() {
        if (mCustomView != null) {
            return (LayoutParams) mCustomView.getLayoutParams();
        }
        return null;
    }

    public void setCustomViewLayoutParams(FrameLayout.LayoutParams layoutParams) {
        if (mCustomView != null) {
            mCustomView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calcOffsetXY();
        drawDrawable(canvas);
        drawIconTagDrawable(canvas);
        drawLabels(canvas);
        drawDiverLine(canvas);
        onExtraDraw(canvas);
    }


    protected void onExtraDraw(Canvas canvas) {
    }

    private void drawDrawable(Canvas canvas) {
        if (mDrawable != null && mIconBoundRect != null) {
            mDrawable.setBounds(mIconBoundRect);
            canvas.save();
            canvas.clipRect(mIconBoundRect);
            mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawIconTagDrawable(Canvas canvas) {
        if (mIconTagDrawable != null && mTagIconBoundRect != null) {
            mIconTagDrawable.setBounds(mTagIconBoundRect);
            canvas.save();
            canvas.clipRect(mTagIconBoundRect);
            mIconTagDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawLabels(Canvas canvas) {
        if (!TextUtils.isEmpty(mTitle)) {
            mTitlePaint.setColor(mTitleColor);
            canvas.drawText(mTitle, mTitleOffsetX, mTitleOffsetY, mTitlePaint);
            if (mSpannableTitle != null && !mSpannableTitle.spans.isEmpty()) {
                mSpannableTitle.drawText(canvas, mTitlePaint, mTitleOffsetX, mTitleOffsetY);
            }
        }
        if (!TextUtils.isEmpty(mDesc)) {
            mDescPaint.setColor(mDescColor);
            canvas.drawText(mDesc, mDescOffsetX, mDescOffsetY, mDescPaint);
        }
        if (!TextUtils.isEmpty(mDesc2)) {
            mDescPaint.setColor(mDesc2Color);
            canvas.drawText(mDesc2, mDescOffsetX, mDescOffsetY2, mDescPaint);
        }
        if (!TextUtils.isEmpty(mValue)) {
            mValuePaint.setTextAlign(valueAlignTo);
            canvas.drawText(mValue, mValueOffsetX, mValueOffsetY, mValuePaint);
        }
    }

    private void drawDiverLine(Canvas canvas) {
        if (mIsShowDiverLine) {
            if (mDiverDrawable != null) {
                canvas.save();
                if (mTopDiverBoundRect != null) {
                    mDiverDrawable.setBounds(mTopDiverBoundRect);
                    canvas.clipRect(mTopDiverBoundRect);
                    mDiverDrawable.draw(canvas);
                }
                if (mBottomDiverBoundRect != null) {
                    mDiverDrawable.setBounds(mBottomDiverBoundRect);
                    canvas.clipRect(mBottomDiverBoundRect);
                    mDiverDrawable.draw(canvas);
                }
                canvas.restore();
            } else {
                //绘制分割线
                mTitlePaint.setColor(mDiverLineColor);
                if ((mDiverLineShowType & DIVER_ALIGN_TOP) != 0) {
                    canvas.drawRect(0, 0, getWidth(), mDiverLineHeight, mTitlePaint);
                }
                if ((mDiverLineShowType & DIVER_ALIGN_BOTTOM) != 0) {
                    canvas.drawRect(0, getHeight() - mDiverLineHeight,
                            getWidth(), getHeight(), mTitlePaint);
                }
            }
        }
    }

    public void setTitle(CharSequence title) {
        if (title == null) {
            mTitle = "";
        } else {
            if (title instanceof SimpleSpannableString) {
                mSpannableTitle = (SimpleSpannableString) title;
                mTitle = mSpannableTitle.text;
            } else {
                mTitle = title.toString();
            }
        }
        mIsComputed = false;
    }

    public void setTitleColor(int color) {
        mTitleColor = color;
        mTitlePaint.setColor(mTitleColor);
    }

    public void setTitleSize(int size) {
        if (mTitleSize != size) {
            mIsComputed = false;
            mTitleSize = size;
        }
    }

    public void setDesc(String value) {
        if (mDesc == null || !mDesc.equals(value)) {
            mIsComputed = false;
            mDesc = value;
        }
    }

    public void setDesc2(String value) {
        if (mDesc2 == null || !mDesc2.equals(value)) {
            mIsComputed = false;
            mDesc2 = value;
        }
    }

    public void setDescColor(int color) {
        mDescColor = color;
    }

    public void setDesc2Color(int color) {
        mDesc2Color = color;
    }

    public void setDescSize(int size) {
        if (mDescSize != size) {
            mDescSize = size;
            mIsComputed = false;
        }
    }

    public void setValue(String value) {
        if (mValue == null || !mValue.equals(value)) {
            mIsComputed = false;
            mValue = value;
        }
    }

    public void setValueColor(int color) {
        mValueColor = color;
    }

    public void setValueSize(int size) {
        if (mValueSize != size) {
            mIsComputed = false;
            mValueSize = size;
        }
    }

    public void setDrawable(Drawable drawable) {
        if (mDrawable != drawable) {
            mIsComputed = false;
            mDrawable = drawable;
            invalidate();
        }
    }

    public void setIconHeight(int iconHeight) {
        if (mIconHeight != iconHeight) {
            mIsComputed = false;
            mIconHeight = iconHeight;
        }
    }

    public void setIconWidth(int iconWidth) {
        if (mIconWidth != iconWidth) {
            mIsComputed = false;
            mIconWidth = iconWidth;
        }
    }

    public void setDrawableAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    protected float getDescOffsetX() {
        return mDescOffsetX;
    }

    protected float getDescOffsetY() {
        return mDescOffsetY;
    }

    protected FontUtil.FontSize getDescSize() {
        if (TextUtils.isEmpty(mDesc)) {
            FontUtil.FontSize fontSize = FontUtil.calcTextSize(mDescPaint, "8");
            fontSize.width = 0;
            return fontSize;
        }
        return FontUtil.calcTextSize(mDescPaint, mDesc);
    }

    public void setPaddingLeft(int paddingLeft) {
        mPaddingLeft = paddingLeft;
    }

    public void setPaddingRight(int paddingRight) {
        mPaddingRight = paddingRight;
    }

    public void setPaddingTop(int paddingTop) {
        mPaddingTop = paddingTop;
    }

    public void setPaddingBottom(int paddingBottom) {
        mPaddingBottom = paddingBottom;
    }


    public void setOnLinearItemClickListener(OnLinearItemClickListener listener) {
        if (listener == null) {
            //todo 如果用户主动设置为null，则认为用户不需要整条的点击效果，需要去掉监听事件
            setOnClickListener(null);
        } else {
            setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(@NotNull View v) {
                    if (mOnLinearItemClickListener != null) {
                        mOnLinearItemClickListener.onItemClick((LinearItemView) v);
                    }
                }
            });
        }
        mOnLinearItemClickListener = listener;
    }

    public void setOnCustomViewClickListener(final OnCustomViewClickListener listener) {
        if (mCustomView != null) {
            switch (mCustomViewType) {
                case CUSTOMVIEW_CHECKBOX:
                case CUSTOMVIEW_SWITCHER: {
                    final CompoundButton checkBox = (CompoundButton) mCustomView;
                    if (listener != null) {
                        checkBox.setClickable(true);
                        checkBox.setEnabled(true);
                        checkBox.setOnCheckedChangeListener(
                                (buttonView, isChecked) -> {
                                    checkBox.setChecked(isChecked);
                                    if (listener != null) {
                                        listener.onCustomViewClick(LinearItemView.this, isChecked);
                                    }
                                });
                    } else {
                        checkBox.setOnCheckedChangeListener(null);
                        checkBox.setClickable(false);
                        checkBox.setEnabled(false);
                    }
                    break;
                }
                case CUSTOMVIEW_MORE:
                case CUSTOMVIEW_LABEL:
                case CUSTOMVIEW_BUTTON: {
                    if (listener != null) {
                        mCustomView.setOnClickListener(v -> {
                            if (listener != null) {
                                listener.onCustomViewClick(LinearItemView.this, null);
                            }
                        });
                    } else {
                        mCustomView.setOnClickListener(null);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 点击整个界面，目前处理是相当于点击customview，如有特殊处理，后续再考虑
     */
    @Override
    public void onClick(View v) {
        if (mOnLinearItemClickListener != null) {
            mOnLinearItemClickListener.onItemClick(this);
        }
    }

    /**
     * 设置customview的初始状态值
     */
    public void setCustomViewValue(Object value) {
        if (mCustomView != null) {
            if (value == null) {
                mCustomView.setVisibility(GONE);
                return;
            }
            mCustomView.setVisibility(VISIBLE);
            if (mCustomView instanceof SwitchCompat) {
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException("The type of value should be Boolean!");
                }
                ((SwitchCompat) mCustomView).setChecked((Boolean) value);
            } else if (mCustomView instanceof CheckBox) {
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException("The type of value should be Boolean!");
                }
                ((CheckBox) mCustomView).setChecked((Boolean) value);
            } else if (mCustomView instanceof RadioButton) {
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException("The type of value should be Boolean!");
                }
                ((RadioButton) mCustomView).setChecked((Boolean) value);
            } else if (mCustomView instanceof TextView) {
                if (value instanceof CharSequence) {
                    ((TextView) mCustomView).setText((CharSequence) value);
                } else if (value instanceof Integer || value instanceof Long) {
                    ((TextView) mCustomView).setText(String.valueOf(value));
                } else {
                    throw new IllegalArgumentException("The type of value should be String or resoureID!");
                }
            } else if (mCustomView instanceof ImageView) {
                if (value instanceof Drawable) {
                    ((ImageView) mCustomView).setImageDrawable((Drawable) value);
                } else {
                    throw new IllegalArgumentException("The type of value should be Drawable!");
                }
            }
            //todo 其他view
        }
    }

    /**
     * 设置customview的背景圖片
     */
    public void setCustomViewBackgroundDrawable(Drawable bgDrawable) {
        if (mCustomView != null) {
            if (mCustomView instanceof TextView) {
                mCustomView.setBackgroundDrawable(bgDrawable);
            }
            //todo 其他view
        }
    }

    /**
     * 设置customview的背景圖片
     */
    public void setCustomViewBackgroundResource(int rid) {
        if (mCustomView != null) {
            if (mCustomView instanceof TextView || mCustomView instanceof ImageView) {
                mCustomView.setBackgroundResource(rid);
            }
            //todo 其他view
        }
    }

    /**
     * 设置customview的字體顏色
     */
    public void setCustomViewValueColor(int color) {
        if (mCustomView != null) {
            if (mCustomView instanceof TextView) {
                ((TextView) mCustomView).setTextColor(color);
            }
            //todo 其他view
        }
    }

    /**
     * 设置customview的字體样式
     */
    public void setCustomViewTypeface(Typeface tf) {
        if (mCustomView != null) {
            if (mCustomView instanceof TextView) {
                ((TextView) mCustomView).setTypeface(tf);
            }
        }
    }

    /**
     * 设置customview的字體大小
     *
     * @param size 单位sp
     */
    public void setCustomViewSize(float size) {
        if (mCustomView != null) {
            if (mCustomView instanceof TextView) {
                ((TextView) mCustomView).setTextSize(size);
            }
        }
    }

    public void setCustomViewType(int type) {
        if (mCustomViewType == type) {
            return;
        }
        mCustomViewType = type;
        initCustomView();
        ViewUtil.viewPostInvalidateOnAnimation(this);
    }

    public void setTitleStyle(int titleStyle) {
        this.mTitleTextStyleIndex = titleStyle;
        setTypeface(mTitleTextStyleIndex, mTitlePaint);
    }

    public void showDiverLine(boolean isshow) {
        mIsShowDiverLine = isshow;
    }

    public void showDiverLine(boolean isShow, int showType) {
        mIsShowDiverLine = isShow;
        mDiverLineShowType = showType;
    }

    public void setValueTextAlign(Paint.Align valueAlignTo) {
        this.valueAlignTo = valueAlignTo;
    }

    public void setIconTagDrawable(Drawable tagDrawable) {
        if (mIconTagDrawable != tagDrawable) {
            mIsComputed = false;
            this.mIconTagDrawable = tagDrawable;
            invalidate();
        }
    }

    public void setValuePaintFlags(int flags) {
        if (mValuePaint.getFlags() != flags) {
            mValuePaint.setFlags(flags);
        }
    }

    public int getValuePaintFlags() {
        return mValuePaint.getFlags();
    }

    public void setTitlePaintFlags(int flags) {
        if (mTitlePaint.getFlags() != flags) {
            mTitlePaint.setFlags(flags);
        }
    }

    public int getTitlePaintFlags() {
        return mTitlePaint.getFlags();
    }

    public void setDescPaintFlags(int flags) {
        if (mDescPaint.getFlags() != flags) {
            mDescPaint.setFlags(flags);
        }
    }

    public int getDescPaintFlags() {
        return mDescPaint.getFlags();
    }

    /**
     * 对整个view的点击监听事件
     */
    public interface OnLinearItemClickListener {
        void onItemClick(LinearItemView view);
    }

    /**
     * 对CustomView的点击监听事件
     */
    public interface OnCustomViewClickListener<T> {
        void onCustomViewClick(LinearItemView view, T result);
    }

    /**
     * 支持简单的富文本,目前只支持指定子字符串颜色
     */
    public static class SimpleSpannableString implements CharSequence {
        String text;
        List<SpanItem> spans = new ArrayList<>();

        public SimpleSpannableString(String text) {
            this.text = text;
        }

        public SimpleSpannableString setSpan(int start, int end, int color) {
            spans.add(new SpanItem(start, end, color));
            return this;
        }

        public void drawText(Canvas canvas, TextPaint paint, float offsetX, float offsetY) {
            for (SpanItem item : spans) {
                paint.setColor(item.color);
                item.calcOffset(paint);
                canvas.drawText(item.text, offsetX + item.offsetX, offsetY, paint);
            }
        }

        @Override
        public int length() {
            return text.length();
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public char charAt(int index) {
            return text.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return text.subSequence(start, end);
        }

        @Override
        public IntStream chars() {
            return null;
        }

        @Override
        public IntStream codePoints() {
            return null;
        }

        class SpanItem {
            int start;
            int end;
            int color;
            float offsetX;
            String text;

            SpanItem(int start, int end, int color) {
                this.start = start;
                this.end = end;
                this.color = color;
                this.text = SimpleSpannableString.this.text.substring(start, end);
            }

            void calcOffset(TextPaint paint) {
                if (offsetX == 0) {
                    offsetX = FontUtil.calcTextWidth(paint, SimpleSpannableString.this.text.substring(0, start));
                }
            }
        }
    }

    @BindingAdapter("LIV_valueText")
    public static void setBindingValue(LinearItemView view, @Nullable String value) {
        view.setValue(value);
        view.invalidate();
    }

    @BindingAdapter("CustomViewValue")
    public static void setBindingCustomViewValue(LinearItemView view, @Nullable Object value) {
        view.setCustomViewValue(value);
    }

    @BindingAdapter("OnLinearItemClick")
    public static void setBindingOnLinearItemClickListener(LinearItemView view, @Nullable OnLinearItemClickListener listener) {
        view.setOnLinearItemClickListener(listener);
    }

    @BindingAdapter("OnCustomViewClick")
    public static <T> void setBindingOnCustomViewClickListener(LinearItemView view, @Nullable OnCustomViewClickListener<T> listener) {
        view.setOnCustomViewClickListener(listener);
    }
}


