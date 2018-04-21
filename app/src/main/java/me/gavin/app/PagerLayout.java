package me.gavin.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import me.gavin.app.model.Page;
import me.gavin.base.function.Consumer;
import me.gavin.text.R;

/**
 * PagerLayout
 *
 * @author gavin.xiong 2018/4/21
 */
public class PagerLayout extends ViewGroup {

    private final PageView[] mItems = new PageView[Config.pageCount]; // last -> curr -> next

    private final static int SCROLL_TARGET_NONE = 0;
    private final static int SCROLL_TARGET_CURR = 1;
    private final static int SCROLL_TARGET_LAST = 2;
    private int mScrollTarget; // 当前滑动页面

    private float mXFlag;
    private VelocityTracker mVelocityTracker; // 滑动速度跟踪器
    private ObjectAnimator mAnimator; // 翻页动画
    private boolean mTouching; // 触摸中 - 手指是否在屏幕上

    private Consumer<Boolean> onPageChangeCallback;

    public PagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnPageChangeCallback(Consumer<Boolean> callback) {
        this.onPageChangeCallback = callback;
    }

    public void set(Page last, Page curr, Page next) {
        mItems[0].set(last);
        mItems[1].set(curr);
        mItems[2].set(next);

        mItems[0].setTranslationX(-getWidth());
        mItems[1].setTranslationX(0);
        mItems[2].setTranslationX(0);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PagerLayout);

        mItems[0] = new PageView(context, attrs);
        mItems[0].setElevation(Config.pageElevation * 2);
        addView(mItems[0]);

        mItems[1] = new PageView(context, attrs);
        mItems[1].setElevation(Config.pageElevation);
        addView(mItems[1]);

        mItems[2] = new PageView(context, attrs);
        mItems[2].setElevation(0);
        addView(mItems[2]);

        ta.recycle();

        mAnimator = new ObjectAnimator();
        mAnimator.setProperty(TRANSLATION_X);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mTouching) {
                    if (mScrollTarget == SCROLL_TARGET_CURR && (float) mAnimator.getAnimatedValue() == -getWidth()) {
                        if (onPageChangeCallback != null && !mItems[2].isNull()) {
                            onPageChangeCallback.accept(false);
                        }
                    } else if (mScrollTarget == SCROLL_TARGET_LAST && (float) mAnimator.getAnimatedValue() == 0) {
                        if (onPageChangeCallback != null && !mItems[0].isNull()) {
                            onPageChangeCallback.accept(true);
                        }
                    }
                }
                mScrollTarget = SCROLL_TARGET_NONE;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < Config.pageCount; i++) {
            mItems[Config.pageCount - i - 1].layout(l, t, r, b);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                    mXFlag = mItems[1].getTranslationX() - event.getX();
                } else if (mScrollTarget == SCROLL_TARGET_LAST) {
                    mXFlag = mItems[0].getTranslationX() - event.getX();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                if (mScrollTarget == SCROLL_TARGET_NONE) {
                    if (event.getX() - mXFlag <= -Config.touchSlop) {
                        mScrollTarget = SCROLL_TARGET_CURR;
                        mXFlag = mItems[1].getTranslationX() - event.getX();
                    } else if (event.getX() - mXFlag >= Config.touchSlop) {
                        mScrollTarget = SCROLL_TARGET_LAST;
                        mXFlag = mItems[0].getTranslationX() - event.getX();
                    }
                } else if (mScrollTarget == SCROLL_TARGET_CURR) {
                    mItems[1].setTranslationX(mXFlag + event.getX());
                } else if (mScrollTarget == SCROLL_TARGET_LAST) {
                    mItems[0].setTranslationX(mXFlag + event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouching = false;
                mVelocityTracker.addMovement(event);
                startFlipAnim(mScrollTarget == SCROLL_TARGET_LAST ? mItems[0] : mItems[1]);
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
        mAnimator.setTarget(target);
        mVelocityTracker.computeCurrentVelocity(1000);
        float xv = mVelocityTracker.getXVelocity();
        boolean isIn = Math.abs(xv) >= Config.flingVelocity && xv > 0
                || Math.abs(xv) < Config.flingVelocity && target.getTranslationX() > -getWidth() / 2;
        mAnimator.setFloatValues(isIn ? 0 : -getWidth());
        float distance = Math.abs(target.getTranslationX() - (isIn ? 0 : -getWidth()));
        mAnimator.setDuration(Math.round(distance * Config.flipAnimDuration));
        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }
}
