package me.gavin.app.search;

import android.support.annotation.NonNull;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 搜索结果列表更新回调
 *
 * @author gavin.xiong 2018/5/22
 */
class DiffUpdateCallback implements ListUpdateCallback {

    private final LinearLayoutManager mLayoutManager;
    private final RecyclerView.Adapter mAdapter;

    DiffUpdateCallback(@NonNull RecyclerView recycler) {
        if (recycler.getLayoutManager() == null)
            throw new IllegalArgumentException("LayoutManager cannot be null!");
        if (!(recycler.getLayoutManager() instanceof LinearLayoutManager))
            throw new IllegalArgumentException("LayoutManager must be be LinearLayoutManager!");
        if (recycler.getAdapter() == null)
            throw new IllegalArgumentException("Adapter cannot be null!");
        this.mLayoutManager = (LinearLayoutManager) recycler.getLayoutManager();
        this.mAdapter = recycler.getAdapter();
    }

    @Override
    public void onInserted(int position, int count) {
        boolean reset = mLayoutManager.findFirstVisibleItemPosition() == 0;
        mAdapter.notifyItemRangeInserted(position, count);
        if (reset) mLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        boolean reset = mLayoutManager.findFirstVisibleItemPosition() == 0;
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        if (reset) mLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        mAdapter.notifyItemRangeChanged(position, count, payload);
    }
}
