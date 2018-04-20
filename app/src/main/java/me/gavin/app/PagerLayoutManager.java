package me.gavin.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import me.gavin.util.L;

/**
 * 自定义 LayoutManager - 分页展示
 *
 * @author gavin.xiong 2018/4/18
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager {

    private int mCurrentPosition;
    private int mCurrentPositionIndex;

    private int mScrollState;

    private ValueAnimator mAnimator;
    private boolean mAnimFlag;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        L.e("onLayoutChildren - " + mCurrentPosition);
        removeAndRecycleAllViews(recycler);

        if (state.getItemCount() == 0 || state.isPreLayout()) {
            return;
        }

        int itemCount = getItemCount();
        if (itemCount == 0) return;

        mCurrentPositionIndex = 0;

        if (mCurrentPosition < itemCount - 1) {
            View next = recycler.getViewForPosition(mCurrentPosition + 1);
            addView(next);
            mCurrentPositionIndex = 1;
            measureChildWithMargins(next, 0, 0);
            layoutDecoratedWithMargins(next, 0, 0, getWidth(), getHeight());
            next.setElevation(0);
        }

        View curr = recycler.getViewForPosition(mCurrentPosition);
        addView(curr);
        measureChildWithMargins(curr, 0, 0);
        layoutDecoratedWithMargins(curr, 0, 0, getWidth(), getHeight());
        curr.setElevation(Config.elevation);

        if (mCurrentPosition > 0) {
            View last = recycler.getViewForPosition(mCurrentPosition - 1);
            addView(last);
            measureChildWithMargins(last, 0, 0);
            layoutDecoratedWithMargins(last, -getWidth(), 0, 0, getHeight());
            last.setElevation(Config.elevation * 2);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        L.e("onScrollStateChanged - " + state);
        mScrollState = state;
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            mAnimFlag = false;
        }

//        if (state != RecyclerView.SCROLL_STATE_DRAGGING) {
//            View curr = getChildAt(mCurrentPositionIndex);
//            if (curr != null) {
//                if (curr.getLeft() < -getWidth() / 2) {
//                    curr.offsetLeftAndRight(-getWidth() - curr.getLeft());
//                    mCurrentPosition++;
//                    requestLayout();
//                }
//            }
//            View last = getChildAt(mCurrentPositionIndex + 1);
//            if (last != null) {
//                if (last.getLeft() > -getWidth() / 2) {
//                    last.offsetLeftAndRight(0 - last.getLeft());
//                    mCurrentPosition--;
//                    requestLayout();
//                }
//            }
//        }
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        if (positionStart <= mCurrentPosition) {
            mCurrentPosition += itemCount;
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return mScrollState != RecyclerView.SCROLL_STATE_SETTLING || !mAnimFlag;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        L.e("scrollHorizontallyBy - " + mCurrentPositionIndex + " - " + getItemCount());
//        detachAndScrapAttachedViews(recycler);

        if (mCurrentPositionIndex < 1 || mCurrentPositionIndex >= getItemCount() - 1) {
            return dx;
        }

        View curr = getChildAt(mCurrentPositionIndex);
        View last = getChildAt(mCurrentPositionIndex + 1);

        if (mScrollState == RecyclerView.SCROLL_STATE_SETTLING) {
            mAnimator = ValueAnimator.ofInt(curr.getLeft(), dx > 0 ? -getWidth() : 0)
                    .setDuration(360);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.addUpdateListener(animation -> {
                curr.setLeft((int) animation.getAnimatedValue());
                curr.setRight(curr.getLeft() + getWidth());
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (dx > 0) {
                        mCurrentPosition++;
                    }
                    onLayoutChildren(recycler, state);
                }
            });
            mAnimator.start();
            mAnimFlag = true;
            return dx;
        }

        if (dx > 0) { // 左滑
            if (last != null && last.getLeft() > -getWidth()) {
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
            } else if (last != null) {
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
