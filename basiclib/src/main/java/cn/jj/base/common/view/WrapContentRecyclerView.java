package cn.jj.base.common.view;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.epoxy.EpoxyRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrapContentRecyclerView extends EpoxyRecyclerView {
    public WrapContentRecyclerView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WrapContentRecyclerView(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentRecyclerView(@NotNull Context context) {
        super(context);
    }

    private int mMaxHeight = Integer.MAX_VALUE >> 2;
    private int mMaxWidth = Integer.MAX_VALUE >> 2;

    public void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
        }
    }

    public void setMaxWidth(int maxWidth) {
        if (mMaxWidth != maxWidth) {
            this.mMaxWidth = maxWidth;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        );
    }
}
