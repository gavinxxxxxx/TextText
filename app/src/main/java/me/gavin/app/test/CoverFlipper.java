package me.gavin.app.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import me.gavin.app.Config;
import me.gavin.app.model.Word;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public class CoverFlipper extends Flipper {

    private final static int SCROLL_TARGET_NONE = 0;
    private final static int SCROLL_TARGET_CURR = 1;
    private final static int SCROLL_TARGET_LAST = 2;
    private int mScrollTarget; // 当前滑动页面

    private float mXFlag;
    private VelocityTracker mVelocityTracker; // 滑动速度跟踪器
    private ValueAnimator mAnimator; // 翻页动画
    private boolean mTouching; // 触摸中 - 手指是否在屏幕上

    private final PointF mPoint = new PointF();
    private float mOffsetX;

    public CoverFlipper() {
        mAnimator = new ValueAnimator();
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mTouching) {
                    if (mScrollTarget == SCROLL_TARGET_CURR && (float) mAnimator.getAnimatedValue() == -Config.width) {
                        if (onPageChangeCallback != null && mPages[2] != null) {
                            onPageChangeCallback.accept(false);
                        }
                    } else if (mScrollTarget == SCROLL_TARGET_LAST && (float) mAnimator.getAnimatedValue() == 0) {
                        if (onPageChangeCallback != null && mPages[0] != null) {
                            onPageChangeCallback.accept(true);
                        }
                    }
                }
                mScrollTarget = SCROLL_TARGET_NONE;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPoint.set(event.getX(), event.getY());
                mTouching = true;
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);

                if (mAnimator != null && mAnimator.isRunning()) {
                    int target = mScrollTarget;
                    mAnimator.cancel();
                    mScrollTarget = target;
                }
                if (mScrollTarget == SCROLL_TARGET_NONE) {
                    mXFlag = event.getX();
                } else if (mScrollTarget == SCROLL_TARGET_CURR) {
                    mXFlag = mOffsetX - event.getX();
                } else if (mScrollTarget == SCROLL_TARGET_LAST) {
                    mXFlag = mOffsetX - event.getX();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                mOffsetX = event.getX() - mPoint.x;
//                mView.invalidate();

                mVelocityTracker.addMovement(event);

                if (mScrollTarget == SCROLL_TARGET_NONE) {
                    if (event.getX() - mXFlag <= -Config.touchSlop) {
                        mScrollTarget = SCROLL_TARGET_CURR;
                        mXFlag = mOffsetX - event.getX();
                    } else if (event.getX() - mXFlag >= Config.touchSlop) {
                        mScrollTarget = SCROLL_TARGET_LAST;
                        mXFlag = mOffsetX - event.getX();
                    }
                } else if (mScrollTarget == SCROLL_TARGET_CURR) {
                    mOffsetX = mXFlag + event.getX();
                    mView.invalidate();
                } else if (mScrollTarget == SCROLL_TARGET_LAST) {
                    mOffsetX = mXFlag + event.getX();
                    mView.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouching = false;
                mVelocityTracker.addMovement(event);
//                startFlipAnim(mScrollTarget == SCROLL_TARGET_LAST ? mItems[0] : mItems[1]);
                startFlipAnim(null);
                mVelocityTracker.recycle();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 启动翻页动画
     *
     * @param target 动画对象
     */
    private void startFlipAnim(View target) {
//        mAnimator.setTarget(target);
        mVelocityTracker.computeCurrentVelocity(1000);
        float xv = mVelocityTracker.getXVelocity();
        boolean isIn = Math.abs(xv) >= Config.flingVelocity && xv > 0
                || Math.abs(xv) < Config.flingVelocity && mOffsetX > -Config.width / 2;
        mAnimator.setFloatValues(isIn ? 0 : -Config.width);
        float distance = Math.abs(mOffsetX - (isIn ? 0 : -Config.width));
        mAnimator.setDuration(Math.round(distance * Config.flipAnimDuration));
        mAnimator.addUpdateListener(animation -> {
            mOffsetX = ((float)animation.getAnimatedValue());
            mView.invalidate();
        });
        mAnimator.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mScrollTarget == SCROLL_TARGET_CURR
                && Math.abs(mOffsetX) > Config.leftPadding
                && mPages[2] != null) {
            for (Word word : mPages[2].wordList) {
                word.draw(canvas, 0, 0);
            }
        }

        canvas.drawRect(mScrollTarget == SCROLL_TARGET_CURR ? mOffsetX : 0, 0,
                Config.width + (mScrollTarget == SCROLL_TARGET_CURR ? mOffsetX : 0), Config.height, Config.bgPaint);
        if (mPages[1] != null) {
            for (Word word : mPages[1].wordList) {
                word.draw(canvas, mScrollTarget == SCROLL_TARGET_CURR ? mOffsetX : 0, 0);
            }
        }

        if (mScrollTarget == SCROLL_TARGET_LAST) {
            canvas.drawRect(-Config.width + mOffsetX, 0,  mOffsetX, Config.height, Config.bgPaint);
            if (mPages[0] != null) {
                for (Word word : mPages[0].wordList) {
                    word.draw(canvas, -Config.width + mOffsetX, 0);
                }
            }
        }

        canvas.drawText(mPages[1].pageStart + "~" + mPages[1].pageEnd, 10, 40, Config.textPaint);
    }
}
