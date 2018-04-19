package me.gavin.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.gavin.util.L;

/**
 * 自定义 LayoutManager - 分页展示
 *
 * @author gavin.xiong 2018/4/18
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager {

    private int mCurrentPosition;
    private int mCurrentPositionIndex;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        L.e("onLayoutChildren - " + mCurrentPosition);
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) return;

        mCurrentPositionIndex = 0;

        if (mCurrentPosition < itemCount - 1) {
            View next = recycler.getViewForPosition(mCurrentPosition + 1);
            addView(next);
            mCurrentPositionIndex = 1;
            measureChildWithMargins(next, 0, 0);
            layoutDecoratedWithMargins(next, 0, 0, getWidth(), getHeight());
        }

        View curr = recycler.getViewForPosition(mCurrentPosition);
        addView(curr);
        measureChildWithMargins(curr, 0, 0);
        layoutDecoratedWithMargins(curr, 0, 0, getWidth(), getHeight());

        if (mCurrentPosition > 0) {
            View last = recycler.getViewForPosition(mCurrentPosition - 1);
            addView(last);
            measureChildWithMargins(last, 0, 0);
            layoutDecoratedWithMargins(last, -getWidth(), 0, 0, getHeight());
        }

//        if (itemCount < 3) return;
//        int currPosition = 1;
//
//        View next = recycler.getViewForPosition(currPosition + 1);
//        addView(next);
//        measureChildWithMargins(next, 0, 0);
//        layoutDecoratedWithMargins(next, 0, 0, getWidth(), getHeight());
//
//        View curr = recycler.getViewForPosition(currPosition);
//        addView(curr);
//        measureChildWithMargins(curr, 0, 0);
//        layoutDecoratedWithMargins(curr, 0, 0, getWidth(), getHeight());
//
//        View last = recycler.getViewForPosition(currPosition - 1);
//        addView(last);
//        measureChildWithMargins(last, 0, 0);
//        layoutDecoratedWithMargins(last, -getWidth(), 0, 0, getHeight());
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        L.e("onItemsAdded - "+ positionStart + " - " + itemCount);
        if (positionStart <= mCurrentPosition) {
            mCurrentPosition += itemCount;
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        L.e("scrollHorizontallyBy - " + mCurrentPositionIndex + " - " + getItemCount());
//        detachAndScrapAttachedViews(recycler);
        if (mCurrentPositionIndex < 1 || mCurrentPositionIndex >= getItemCount() - 1) {
            return 0;
        }

        View curr = getChildAt(mCurrentPositionIndex);
        View last = getChildAt(mCurrentPositionIndex + 1);

        if (dx > 0) { // 左滑
            if (last.getLeft() > -getWidth()) {
                last.offsetLeftAndRight(-dx);
            } else {
                curr.offsetLeftAndRight(-dx);
                if (curr.getLeft() <= -getWidth()) {
                    mCurrentPosition++;
                    onLayoutChildren(recycler, state);
                }
            }
        } else { // 右滑
            if (curr.getLeft() < 0) {
                curr.offsetLeftAndRight(-dx);
            } else {
                last.offsetLeftAndRight(-dx);
                if (last.getLeft() >= 0) {
                    mCurrentPosition--;
                    onLayoutChildren(recycler, state);
                }
            }
        }

//        if (dx > 0) {
//            curr.offsetLeftAndRight(-dx);
//        } else {
//            last.offsetLeftAndRight(-dx);
//        }
//        offsetChildrenHorizontal(-dx);
//        fill(recycler, state);
        return dx;
    }
}
