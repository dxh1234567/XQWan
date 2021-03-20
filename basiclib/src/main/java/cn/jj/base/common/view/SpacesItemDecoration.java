package cn.jj.base.common.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildLayoutPosition(view);
        outRect.left = (pos % 2 == 0) ? space : space / 2;
        outRect.right = (pos % 2 == 0) ? space / 2 : space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (pos == 0 || pos == 1)
            outRect.top = space;
    }
}