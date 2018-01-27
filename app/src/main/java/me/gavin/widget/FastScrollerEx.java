package me.gavin.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.MotionEvent;

import me.gavin.util.DisplayUtil;

/**
 * RecyclerView 快速滑动器
 * {@link android.support.v7.widget.FastScroller}
 *
 * @author gavin.xiong 2018/1/25
 */
public class FastScrollerEx extends ItemDecoration implements OnItemTouchListener {

    private final RecyclerView mRecyclerView;
    private int mRecyclerViewWidth, mRecyclerViewHeight;
    private final int mScrollbarWidth, mScrollHeight;
    private float mScrollbarY; // scrollbar 顶部 y
    private boolean mIsDrag = false; // 正在拖拽

    private final Paint mPaint;

    public FastScrollerEx(RecyclerView recyclerView) {
        mScrollbarWidth = DisplayUtil.dp2px(8);
        mScrollHeight = DisplayUtil.dp2px(56);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.mRecyclerView = recyclerView;
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateScrollbar();
            }
        });
    }

    /**
     * 触摸事件拦截
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final boolean handled;
        boolean insideVerticalThumb = isPointInsideThumb(e.getX());
        if (e.getAction() == MotionEvent.ACTION_DOWN && insideVerticalThumb) {
            mIsDrag = true;
            handled = true;
        } else {
            handled = mIsDrag;
        }
        rv.requestDisallowInterceptTouchEvent(handled);
        return handled;
    }

    /**
     * 触摸事件
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE && mIsDrag) {
            mScrollbarY = Math.max(e.getY() - mScrollHeight / 2f, 0);
            mScrollbarY = Math.min(mScrollbarY, mRecyclerViewHeight - mScrollHeight);
            scrollTo();
        } else if (mIsDrag) {
            mIsDrag = false;
        }
    }

    /**
     * 这里是萌萌哒注释君
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }

    /**
     * 画 scrollbar
     */
    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mRecyclerViewWidth != mRecyclerView.getWidth() || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
            // 这是由于键盘打开或缩回与旋转时不同的事件顺序。 因此，为避免出现转角情况，
            // 我们只需在尺寸更改时禁用滚动条，然后等待滚动位置重新计算后再显示。
            // setState(STATE_HIDDEN);
            return;
        }
        mPaint.setColor(0x20000000);
        canvas.drawRect(mRecyclerViewWidth - mScrollbarWidth, 0, mRecyclerViewWidth, mRecyclerViewHeight, mPaint);
        mPaint.setColor(mIsDrag ? 0xFF0098E2 : 0xFFAAAAAA);
        canvas.drawRect(mRecyclerViewWidth - mScrollbarWidth, mScrollbarY,
                mRecyclerViewWidth, mScrollbarY + mScrollHeight, mPaint);
    }

    /**
     * 是否响应拖拽事件
     */
    private boolean isPointInsideThumb(float x) {
        return x >= mRecyclerViewWidth - mScrollbarWidth * 2;
    }

    /**
     * recyclerView 定位
     */
    private void scrollTo() {
        int offset = (int) (mScrollbarY / (mRecyclerViewHeight - mScrollHeight)
                * (mRecyclerView.computeVerticalScrollRange() - mRecyclerView.computeVerticalScrollExtent()));
        int oldOffset = mRecyclerView.computeVerticalScrollOffset();
        mRecyclerView.scrollBy(0, offset - oldOffset); // TODO: 2018/1/27 大量数据时频繁 scrollBy 效率低
        // mRecyclerView.invalidate();
    }

    /**
     * 更新 scrollbar 位置
     */
    private void updateScrollbar() {
        // maxOffset + extent = range
        // extent + paddingTB = getHeight
        if (!mIsDrag) {
            mScrollbarY = (float) mRecyclerView.computeVerticalScrollOffset()
                    / (mRecyclerView.computeVerticalScrollRange() - mRecyclerView.computeVerticalScrollExtent())
                    * (mRecyclerViewHeight - mScrollHeight);
        }
    }

}
