package cn.jj.base.common.view;

import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CyclicAdapter extends Adapter {

    private static final int INFINITE_RATIO = 400;

    private final Adapter adapter;
    private final AdapterDataObserver observerDelegate = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            adapter.notifyItemRangeChanged(adjustedPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            adapter.notifyItemRangeChanged(adjustedPosition(positionStart), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            adapter.notifyItemRangeInserted(adjustedPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            adapter.notifyItemRangeRemoved(adjustedPosition(positionStart), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            // Unsupported
        }
    };

    /**
     * Pass in an adapter to wrap. This nested adapter should contain whatever you want to show
     */
    public CyclicAdapter(Adapter adapter) {
        this.adapter = adapter;

        super.setHasStableIds(adapter.hasStableIds());
        super.registerAdapterDataObserver(observerDelegate);
    }

    public int getActualItemCount() {
        return adapter.getItemCount();
    }

    /**
     * Do Not Use!!!
     * <p>
     * This returns {@link Integer#MAX_VALUE} in order to create an infinite looping adapter.
     * <p>
     * Use {@link #getActualItemCount()} instead.
     */
    @Override
    public int getItemCount() {
        int count = getActualItemCount();
        return count <= 1 ? count : INFINITE_RATIO;
    }

    private int adjustedPosition(int position) {
        int count = getActualItemCount();
        return count == 0 ? 0 : position % count;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        adapter.onBindViewHolder(holder, adjustedPosition(position));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List payloads) {
        adapter.onBindViewHolder(holder, adjustedPosition(position), payloads);
    }

    @Override
    public int getItemViewType(int position) {
        return adapter.getItemViewType(adjustedPosition(position));
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        adapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return adapter.getItemId(adjustedPosition(position));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        adapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        return adapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        adapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        adapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void registerAdapterDataObserver(AdapterDataObserver observer) {
        adapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
        adapter.unregisterAdapterDataObserver(observer);
    }
}