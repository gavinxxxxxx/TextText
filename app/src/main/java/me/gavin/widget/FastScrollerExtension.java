package me.gavin.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.MotionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.gavin.util.DisplayUtil;

/**
 * Class responsible to animate and provide a fast scroller.
 * {@link android.support.v7.widget.FastScroller}
 *
 * @author gavin.xiong 2018/1/25
 */
public class FastScrollerExtension extends ItemDecoration implements OnItemTouchListener {

    @IntDef({STATE_HIDDEN, STATE_VISIBLE, STATE_DRAGGING})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    private static final int STATE_HIDDEN = 0; // Scroll thumb not showing
    private static final int STATE_VISIBLE = 1; // Scroll thumb visible and moving along with the scrollbar
    private static final int STATE_DRAGGING = 2; // Scroll thumb being dragged by user

    @IntDef({ANIMATION_STATE_OUT, ANIMATION_STATE_FADING_IN, ANIMATION_STATE_IN, ANIMATION_STATE_FADING_OUT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface AnimationState {
    }

    private static final int ANIMATION_STATE_OUT = 0;
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_FADING_OUT = 3;

    private static final int SHOW_DURATION_MS = 500;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DURATION_MS = 500;
    private static final int SCROLLBAR_FULL_OPAQUE = 255;

    private final Paint mPaint;
    private int mAlpha = SCROLLBAR_FULL_OPAQUE;

    // Final values for the vertical scroll bar
    private final int mVerticalThumbWidth;

    // Dynamic values for the vertical scroll bar
    private int mVerticalThumbHeight;
    private int mVerticalThumbCenterY;
    private float mVerticalDragY;

    private int mRecyclerViewWidth = 0;
    private int mRecyclerViewHeight = 0;

    private RecyclerView mRecyclerView;

    private boolean mNeedVerticalScrollbar = false;
    @State
    private int mState = STATE_HIDDEN;
    private boolean mIsDrag = false;

    private final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(0, 1);
    @AnimationState
    private int mAnimationState = ANIMATION_STATE_OUT;
    private final Runnable mHideRunnable = this::hide;

    public FastScrollerExtension(RecyclerView recyclerView) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mVerticalThumbWidth = DisplayUtil.dp2px(8);
        mVerticalThumbHeight = DisplayUtil.dp2px(56);

        mShowHideAnimator.addListener(new AnimatorListener());
        mShowHideAnimator.addUpdateListener(new AnimatorUpdater());

        mRecyclerView = recyclerView;
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // maxOffset + extent = range
                updateScrollPosition(recyclerView.computeVerticalScrollOffset());
            }
        });
    }

    private void requestRedraw() {
        mRecyclerView.invalidate();
    }

    private void setState(@State int state) {
        if (state == STATE_DRAGGING && mState != STATE_DRAGGING) {
//            mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }

        if (state == STATE_HIDDEN) {
            requestRedraw();
        } else {
            show();
        }

        if (mState == STATE_DRAGGING && state != STATE_DRAGGING) {
//            mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (state == STATE_VISIBLE) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        mState = state;
    }

    private void show() {
        if (mAnimationState == ANIMATION_STATE_FADING_OUT) {
            mShowHideAnimator.cancel();
        } else if (mAnimationState == ANIMATION_STATE_OUT) {
            mAnimationState = ANIMATION_STATE_FADING_IN;
            mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 1);
            mShowHideAnimator.setDuration(SHOW_DURATION_MS);
            mShowHideAnimator.start();
        }
    }

    private void hide() {
        if (mAnimationState == ANIMATION_STATE_FADING_IN) {
            mShowHideAnimator.cancel();
        } else if (mAnimationState == ANIMATION_STATE_IN) {
            mAnimationState = ANIMATION_STATE_FADING_OUT;
            mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 0);
            mShowHideAnimator.setDuration(HIDE_DURATION_MS);
            mShowHideAnimator.start();
        }
    }

    private void cancelHide() {
        mRecyclerView.removeCallbacks(mHideRunnable);
    }

    private void resetHideDelay(int delay) {
        cancelHide();
        mRecyclerView.postDelayed(mHideRunnable, delay);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mRecyclerViewWidth != mRecyclerView.getWidth() || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
            // 这是由于键盘打开或缩回与旋转时不同的事件顺序。 因此，为避免出现转角情况，
            // 我们只需在尺寸更改时禁用滚动条，然后等待滚动位置重新计算后再显示。
            setState(STATE_HIDDEN);
            return;
        }

        if (mAnimationState != ANIMATION_STATE_OUT) {
            if (mNeedVerticalScrollbar) {
                drawVerticalScrollbar(canvas);
            }
        }
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        mPaint.setColor((int) (mAlpha * 0.1) << 24);
        canvas.drawRect(mRecyclerViewWidth - mVerticalThumbWidth, 0, mRecyclerViewWidth, mRecyclerViewHeight, mPaint);
        mPaint.setColor(mAlpha << 24 | (mIsDrag ? 0x0098E2 : 0xAAAAAA));
        canvas.drawRect(mRecyclerViewWidth - mVerticalThumbWidth, mVerticalThumbCenterY - mVerticalThumbHeight / 2,
                mRecyclerViewWidth, mVerticalThumbCenterY + mVerticalThumbHeight / 2, mPaint);
    }

    /**
     * Notify the scroller of external change of the scroll, e.g. through dragging or flinging on
     * the view itself.
     *
     * @param offsetY The new scroll Y offset.
     */
    private void updateScrollPosition(int offsetY) {
        // maxOffset + extent = range
        // extent + paddingTB = getHeight
        int verticalScrollRange = mRecyclerView.computeVerticalScrollRange(); // 垂直滚动条代表的垂直范围
        int verticalHeight = mRecyclerViewHeight;
        mNeedVerticalScrollbar = verticalScrollRange - verticalHeight > 0;

        if (!mNeedVerticalScrollbar) {
            if (mState != STATE_HIDDEN) {
                setState(STATE_HIDDEN);
            }
            return;
        }

        float mVerticalThumbTop = ((float) offsetY / (verticalScrollRange - verticalHeight) * (verticalHeight - mVerticalThumbHeight));
        mVerticalThumbCenterY = (int) (mVerticalThumbTop + mVerticalThumbHeight / 2f);

        if (mState == STATE_HIDDEN || mState == STATE_VISIBLE) {
            setState(STATE_VISIBLE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent ev) {
        final boolean handled;
        if (mState == STATE_VISIBLE) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(ev.getX(), ev.getY());
            if (ev.getAction() == MotionEvent.ACTION_DOWN && insideVerticalThumb) {
                mIsDrag = true;
                mVerticalDragY = (int) ev.getY();

                setState(STATE_DRAGGING);
                handled = true;
            } else {
                handled = false;
            }
        } else {
            handled = mState == STATE_DRAGGING;
        }
        recyclerView.requestDisallowInterceptTouchEvent(handled);
        return handled;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent me) {
        if (mState == STATE_HIDDEN) {
            return;
        }

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(me.getX(), me.getY());
            if (insideVerticalThumb) {
                mIsDrag = true;
                mVerticalDragY = (int) me.getY();
                setState(STATE_DRAGGING);
            }
        } else if (me.getAction() == MotionEvent.ACTION_UP && mState == STATE_DRAGGING) {
            mVerticalDragY = 0;
            setState(STATE_VISIBLE);
            mIsDrag = false;
        } else if (me.getAction() == MotionEvent.ACTION_MOVE && mState == STATE_DRAGGING) {
            show();
            if (mIsDrag) {
                verticalScrollTo(me.getY());
            }
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void verticalScrollTo(float y) {
        if (Math.abs(mVerticalThumbCenterY - y) < 2) {
            return;
        }
        int scrollingBy = scrollTo(mVerticalDragY, y,
                mRecyclerView.computeVerticalScrollRange(),
                mRecyclerView.computeVerticalScrollOffset(), mRecyclerViewHeight);
        if (scrollingBy != 0) {
            mRecyclerView.scrollBy(0, scrollingBy);
        }
        mVerticalDragY = y;
    }

    private int scrollTo(float oldDragPos, float newDragPos, int scrollRange,
                         int scrollOffset, int viewLength) {
        float percentage = ((newDragPos - oldDragPos) / (float) (mRecyclerViewHeight - mVerticalThumbHeight));
        int totalPossibleOffset = scrollRange - viewLength;
        int scrollingBy = (int) (percentage * totalPossibleOffset);
        int absoluteOffset = scrollOffset + scrollingBy;
        if (absoluteOffset < totalPossibleOffset && absoluteOffset >= 0) {
            return scrollingBy;
        } else {
            return 0;
        }
    }

    /**
     * 是否响应拖拽事件
     */
    private boolean isPointInsideVerticalThumb(float x, float y) {
        return x >= mRecyclerViewWidth - mVerticalThumbWidth * 5
                && y >= mVerticalThumbCenterY - mVerticalThumbHeight / 2
                && y <= mVerticalThumbCenterY + mVerticalThumbHeight / 2;
    }

    private class AnimatorListener extends AnimatorListenerAdapter {

        private boolean mCanceled = false;

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mCanceled) {
                mCanceled = false;
                return;
            }
            if ((float) mShowHideAnimator.getAnimatedValue() == 0) {
                mAnimationState = ANIMATION_STATE_OUT;
                setState(STATE_HIDDEN);
            } else {
                mAnimationState = ANIMATION_STATE_IN;
                requestRedraw();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceled = true;
        }
    }

    private class AnimatorUpdater implements AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mAlpha = (int) (SCROLLBAR_FULL_OPAQUE * ((float) valueAnimator.getAnimatedValue()));
            requestRedraw();
        }
    }
}
