package com.jj.base.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.core.content.ContextCompat;

import com.jj.base.common.ThreadManager;
import com.jj.base.utils.FontUtil;
import com.jj.base.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import com.jj.basiclib.R;

import static com.jj.base.common.view.WheelStyle.STYLE_DAY;
import static com.jj.base.common.view.WheelStyle.STYLE_HOUR;
import static com.jj.base.common.view.WheelStyle.STYLE_MINUTE;
import static com.jj.base.common.view.WheelStyle.STYLE_MONTH;
import static com.jj.base.common.view.WheelStyle.STYLE_YEAR;


/**
 * Created by yangxl on 2016/10/23.
 */

public final class WheelView extends View {
    private static final int DEFAULT_ITEM_HEIGHT = 40;  //dp
    private static int AUTO_SCROLLTO_CENTER_DURATION = 600;
    private static int FLING_SCROLL_DURATION = 1000;
    /**
     * 除选中item外，上下各需要显示的备选项数目,必须大于等于1
     */
    private int upDownShowSize;
    private List<String> itemList;
    private int itemCount;
    /**
     * item高度
     */
    private int itemHeight;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int currentSelectedItem;
    private float currentMoveDistance;

    private Paint drawItemPaint;
    private int selectedTextColor;
    private float selectedTextSize;
    /**
     * 中心位置上下两个item字体颜色和大小
     */
    private int unselectedTextColor;
    private float unselectedTextSize;
    /**
     * 除中心以及上下两个之外的item字体颜色和大小
     */
    private int edgeTextColor;
    private float edgeTextSize;
    private int diverLineColor;
    private float diverLineWidth;    //0表示与wheelview同宽
    private float diverLineWidthRatio;    //相对view整体宽度的占用比
    private float diverLineOffsetX;

    private float centerY;
    private float centerX;

    /**
     *
     * */
    private int wheelStyle = WheelStyle.STYLE_NONE;
    /**
     * 实现惯性滑动
     */
    private float lastMotionEventY;
    private Scroller scroller;
    private VelocityTracker velocityTracker = null;
    private int activePointerId;
    private float minFlingVelocity;
    private float maximumVelocity;
    private GestureDetector gestureDetector;
    private float oldScrollerY;
    private int touchSlop;
    /**
     * 判断是否为向上滚动
     */
    private boolean isScrollUp;
    /**
     * 是否显示单位
     */
    private boolean isShowUnitText;
    private boolean isSetDefaultWheelData;

    private OnValueChangeListener onValueChangedListener;
    private Runnable selectedItemChangeTask;
    private boolean isDestroy = true;
    private boolean isNotifyChangeWhenFling = true;

    public WheelView(Context context) {
        super(context);
        init(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        selectedTextSize = a.getDimension(R.styleable.WheelView_selectedTextSize,
                getResources().getDimensionPixelSize(R.dimen.wheel_textsize_selected));
        selectedTextColor = a.getColor(R.styleable.WheelView_selectedTextColor,
                ContextCompat.getColor(context, R.color.basic_wheel_color_selected));
        unselectedTextSize = a.getDimension(R.styleable.WheelView_normalTextSize,
                getResources().getDimensionPixelSize(R.dimen.wheel_textsize_unselected));
        unselectedTextColor = a.getColor(R.styleable.WheelView_normalTextColor,
                ContextCompat.getColor(context, R.color.basic_wheel_color_unselected));
        edgeTextSize = a.getDimension(R.styleable.WheelView_edgeTextSize,
                getResources().getDimensionPixelSize(R.dimen.wheel_textsize_edge));
        edgeTextColor = a.getColor(R.styleable.WheelView_edgeTextColor,
                ContextCompat.getColor(context, R.color.basic_wheel_color_edge));
        diverLineColor = a.getColor(R.styleable.WheelView_splitLineColor,
                ContextCompat.getColor(context, R.color.basic_wheel_color_diverline));

        diverLineWidthRatio = a.getFloat(R.styleable.WheelView_splitLineWidthRatio, 1);
        wheelStyle = a.getInt(R.styleable.WheelView_wheelStyle, WheelStyle.STYLE_NONE);
        upDownShowSize = a.getInt(R.styleable.WheelView_itemNumber, 2);
        itemHeight = (int) a.getDimension(R.styleable.WheelView_itemHeight, ScreenUtil.dp2px(DEFAULT_ITEM_HEIGHT));

        isShowUnitText = a.getBoolean(R.styleable.WheelView_showUnit, false);
        isSetDefaultWheelData = a.getBoolean(R.styleable.WheelView_setDefaultWheelData, true);
        a.recycle();

        if (upDownShowSize < 1) {
            throw new IllegalArgumentException("WheelView: The show size on the both of center item must large or equal 1");
        }

        itemList = new ArrayList<>();
        drawItemPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawItemPaint.setStyle(Style.FILL);
        drawItemPaint.setTextAlign(Align.CENTER);
        scroller = new Scroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        maximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        if (wheelStyle != WheelStyle.STYLE_NONE) {
            setWheelStyle(wheelStyle);
        }
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }

    public void setWheelStyle(int style) {
        wheelStyle = style;
        if (isSetDefaultWheelData &&
                (wheelStyle == STYLE_HOUR ||
                        wheelStyle == STYLE_MINUTE ||
                        wheelStyle == STYLE_YEAR ||
                        wheelStyle == STYLE_MONTH ||
                        wheelStyle == STYLE_DAY)) {
            setWheelItemList(WheelStyle.getItemList(style));
        }
    }

    public void setWheelItemList(List<String> itemList) {
        this.itemList = itemList;
        if (itemList != null) {
            itemCount = itemList.size();
            // 数据列表修改 当前item项继续用当前的index
            resetCurrentSelect(currentSelectedItem, false);
        }
    }

    public void setNotifyChangeWhenFling(boolean notifyChangeWhenFling) {
        isNotifyChangeWhenFling = notifyChangeWhenFling;
    }

    private void resetCurrentSelect(int index, boolean notifyChange) {
        int temp = Math.min(Math.max(0, index), itemCount - 1);
        setCurrentSelectedItem(temp, notifyChange);
        invalidate();
    }

    /**
     * 修改当前选中item
     */
    private void setCurrentSelectedItem(int newValue, boolean notifyChange) {
        if (currentSelectedItem == newValue) {
            return;
        }
        int oldValue = currentSelectedItem;
        currentSelectedItem = newValue;
        if (notifyChange) {
            notifyChange(oldValue, newValue);
        }
    }

    /**
     * 数据修改回调通知
     */
    private void notifyChange(int previous, int current) {
        if (isNotifyChangeWhenFling) {
            if (null != onValueChangedListener) {
                onValueChangedListener.onValueChange(this, previous, current);
            }
        } else {
            ThreadManager.removeUI(selectedItemChangeTask);
            selectedItemChangeTask = () -> {
                if (null != onValueChangedListener && !isDestroy) {
                    onValueChangedListener.onValueChange(this, previous, current);
                }
            };
            //@note 延时200ms执行数据修改回调
            ThreadManager.postUI(selectedItemChangeTask, 200);
        }
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setShowUnitText(boolean isShowUntilText) {
        this.isShowUnitText = isShowUntilText;
    }

    public int getSelectedItem() {
        return currentSelectedItem;
    }

    /**
     * 选择选中的item的index
     *
     * @note 默认情况不需要回调通知
     */
    public void setSelectedItem(int selected, boolean... notifyChange) {
        boolean notify = false;
        if (null != notifyChange && notifyChange.length == 1) {
            notify = notifyChange[0];
        }
        resetCurrentSelect(selected, notify);
    }

    /**
     * 设置选择选中的item
     */
    public void setSelectedItem(String selected, boolean... notifyChange) {
        if (null != itemList) {
            setSelectedItem(itemList.indexOf(selected), notifyChange);
        }
    }

    public String getSelectedItemText() {
        if (!itemList.isEmpty() && itemList.size() > currentSelectedItem) {
            return itemList.get(currentSelectedItem);
        }
        throw new RuntimeException("itemList is empty or not find selected item");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (2 * upDownShowSize + 1) * itemHeight);
        centerX = (float) (getMeasuredWidth() / 2.0);
        centerY = (float) (getMeasuredHeight() / 2.0);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!itemList.isEmpty()) {
            // 绘制中间data
            drawItemPaint.setColor(selectedTextColor);
            drawItemPaint.setTextSize(selectedTextSize);
            drawSelectedItem(canvas);

            drawItemPaint.setColor(diverLineColor);
            drawDiverLine(canvas);

            if (itemList.size() > 1) {
                drawItemPaint.setColor(unselectedTextColor);
                //绘制两边的第一个item
                drawUnselectedItem(canvas);
                //绘制边缘的item
                if (upDownShowSize > 1) {
                    drawItemPaint.setColor(edgeTextColor);
                    for (int i = 2; i < upDownShowSize + 1; i++) {
                        drawItemPaint.setTextSize(edgeTextSize);
                        drawEdgeItem(canvas, i, -1);
                        drawItemPaint.setTextSize(edgeTextSize);
                        drawEdgeItem(canvas, i, 1);
                    }
                }
            }
        }
    }

    private void drawUnselectedItem(Canvas canvas) {
        drawItemPaint.setTextSize(unselectedTextSize);
        drawEdgeItem(canvas, 1, 1);
        drawItemPaint.setTextSize(unselectedTextSize);
        drawEdgeItem(canvas, 1, -1);
    }

    private void drawSelectedItem(Canvas canvas) {
        StringBuilder text = new StringBuilder(itemList.get(currentSelectedItem));
        if (isShowUnitText) {
            String unitText = WheelStyle.getUnitString(getContext(), wheelStyle);
            if (!TextUtils.isEmpty(unitText)) {
                text.append(" ").append(unitText);
            }
        }
        calculateTextWidth(text.toString());
        FontMetricsInt fmi = drawItemPaint.getFontMetricsInt();
        double selectedFontHeight = fmi.bottom / 2.0 + fmi.top / 2.0;
        float baseline = (float) (centerY + currentMoveDistance - selectedFontHeight);
        canvas.drawText(text.toString(), centerX, baseline, drawItemPaint);
    }

    private void calculateTextWidth(String text) {
        int textWidth = FontUtil.calcTextWidth(drawItemPaint, text);
        float textSize = drawItemPaint.getTextSize();
        while (textWidth > getWidth() - getPaddingRight() - getPaddingLeft()) {
            textSize -= ScreenUtil.sp2px(1f);
            drawItemPaint.setTextSize(textSize);
            textWidth = FontUtil.calcTextWidth(drawItemPaint, text);
        }
    }

    private void drawDiverLine(Canvas canvas) {
        if (diverLineWidth == 0) {
            diverLineWidth = diverLineWidthRatio * getWidth();
        }
        if (diverLineOffsetX == 0) {
            diverLineOffsetX = (getWidth() - diverLineWidth) / 2.0f;
        }
        canvas.drawLine(diverLineOffsetX, centerY - itemHeight / 2.0f, diverLineOffsetX + diverLineWidth, centerY - itemHeight / 2.0f, drawItemPaint);
        canvas.drawLine(diverLineOffsetX, centerY + itemHeight / 2.0f, diverLineOffsetX + diverLineWidth, centerY + itemHeight / 2.0f, drawItemPaint);
    }

    /**
     * @param type 1表示向下绘制，-1表示向上绘制
     */
    private void drawEdgeItem(Canvas canvas, int position, int type) {
        int index = currentSelectedItem + type * position;
        if (index >= itemCount) {
            index = index - itemCount;
        } else if (index < 0) {
            index = index + itemCount;
        }
        String text = itemList.get(index);
        calculateTextWidth(text);
        float d = itemHeight * position + type * currentMoveDistance;
        FontMetricsInt fmi = drawItemPaint.getFontMetricsInt();
        double unselectedFontHeight = fmi.bottom / 2.0 + fmi.top / 2.0;
        float baseline = (float) (centerY + type * d - unselectedFontHeight);
        canvas.drawText(text, centerX, baseline, drawItemPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (itemList.size() <= 1) {
            return super.onTouchEvent(event);
        }
        boolean status = gestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (!scroller.isFinished()) {
                    completeScroll();
                }
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                activePointerId = event.getPointerId(0);
                lastMotionEventY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                isScrollUp = event.getY() <= lastMotionEventY;
                currentMoveDistance += (event.getY() - lastMotionEventY);
                updateCurrentItem();
                lastMotionEventY = event.getY();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (!status) {
                    if (Math.abs(currentMoveDistance) > touchSlop) {
                        oldScrollerY = currentMoveDistance;
                        isScrollUp = false;
                        scroller.startScroll(0, (int) currentMoveDistance, 0,
                                -(int) currentMoveDistance, AUTO_SCROLLTO_CENTER_DURATION);
                    } else {
                        currentMoveDistance = 0;
                    }
                } else {
                    oldScrollerY = currentMoveDistance;
                    scroller.startScroll(0, (int) currentMoveDistance, 0,
                            getFlingScrollDistance() + (isScrollUp ? (int) currentMoveDistance : -(int) currentMoveDistance),
                            FLING_SCROLL_DURATION);
                }
                if (velocityTracker != null) {
                    try {
                        velocityTracker.recycle();
                    } catch (Exception e) {
                    }
                    velocityTracker = null;
                }
                invalidate();
                break;
            }
            default:
                break;
        }
        return true;
    }

    private int getFlingScrollDistance() {
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(2 * itemHeight, maximumVelocity);
            float yVelocity = Math.abs(velocityTracker.getYVelocity(activePointerId) / 2);
            if (yVelocity > minFlingVelocity) {
                return itemHeight * (((int) (yVelocity * (FLING_SCROLL_DURATION / 1000) + itemHeight - 1)) / itemHeight);
            }
        }
        return 0;
    }

    private void updateCurrentItem() {
        int temp = currentSelectedItem;
        if (currentMoveDistance > itemHeight >> 1) {
            // 往下滑超过离开距离
            currentMoveDistance = currentMoveDistance - itemHeight;
            temp--;
            if (temp < 0) {
                temp = itemCount - 1;
            }
        } else if (currentMoveDistance < -itemHeight >> 1) {
            // 往上滑超过离开距离
            currentMoveDistance = currentMoveDistance + itemHeight;
            temp++;
            if (temp >= itemCount) {
                temp = 0;
            }
        }
        setCurrentSelectedItem(temp, true);
    }

    private boolean fixCurrentItem() {
        float moveLen = scroller.getCurrY() - oldScrollerY;
        oldScrollerY = scroller.getCurrY();
        if (isScrollUp) {
            currentMoveDistance -= moveLen;
        } else {
            currentMoveDistance += moveLen;
        }
        updateCurrentItem();
        return false;
    }


    @Override
    public void computeScroll() {
        if (!scroller.isFinished() && scroller.computeScrollOffset()) {
            fixCurrentItem();
            invalidate();
            return;
        }
    }

    private void completeScroll() {
        scroller.abortAnimation();
        fixCurrentItem();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isDestroy = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDestroy = true;
        if (!isNotifyChangeWhenFling) {
            ThreadManager.removeUI(selectedItemChangeTask);
            selectedItemChangeTask = null;
        }
    }

    public interface OnValueChangeListener {
        /**
         * oldVal,newVal是数据index，不是数据值
         */
        void onValueChange(WheelView wheelView, int oldVal, int newVal);
    }
}
