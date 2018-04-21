package me.gavin.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import me.gavin.app.model.Page;
import me.gavin.base.function.Consumer;
import me.gavin.text.R;
import me.gavin.util.L;

/**
 * PagerLayout
 *
 * @author gavin.xiong 2018/4/21
 */
public class PagerLayout extends ViewGroup {

    private final PageView[] mItems = new PageView[Config.pageCount]; // last -> curr -> next

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
                if (!mTouchFlag) {
                    if (mScrollTarget == SCROLL_CURR && (float) mAnimator.getAnimatedValue() == -getWidth()) {
                        if (onPageChangeCallback != null && !mItems[2].isNull()) {
                            onPageChangeCallback.accept(false);
                        }
                    } else if (mScrollTarget == SCROLL_LAST && (float) mAnimator.getAnimatedValue() == 0) {
                        if (onPageChangeCallback != null && !mItems[0].isNull()) {
                            onPageChangeCallback.accept(true);
                        }
                    }
                }
                mScrollTarget = SCROLL_NONE;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        L.e("onLayout");
        mItems[2].setTranslationX(0);
        mItems[2].layout(l, t, r, b);

        mItems[1].setTranslationX(0);
        mItems[1].layout(l, t, r, b);

        mItems[0].setTranslationX(-r);
        mItems[0].layout(l, t, r, b);
    }

    private final static int SCROLL_NONE = 0;
    private final static int SCROLL_CURR = 1;
    private final static int SCROLL_LAST = 2;
    private int mScrollTarget;

    private float mXFlag;
    private VelocityTracker mVelocityTracker;
    private ObjectAnimator mAnimator;

    private boolean mTouchFlag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                L.e("onTouchEvent - " + mScrollTarget);
                mTouchFlag = true;

                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);

                if (mAnimator != null) {
                    int target = mScrollTarget;
                    mAnimator.cancel();
                    mScrollTarget = target;
                }
                if (mScrollTarget == SCROLL_NONE) {
                    mXFlag = event.getX();
                } else if (mScrollTarget == SCROLL_CURR) {
                    mXFlag = mItems[1].getTranslationX() - event.getX();
                } else if (mScrollTarget == SCROLL_LAST) {
                    mXFlag = mItems[0].getTranslationX() - event.getX();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                if (mScrollTarget == SCROLL_NONE) {
                    if (event.getX() - mXFlag <= -Config.touchSlop) {
                        mScrollTarget = SCROLL_CURR;
                        mXFlag = mItems[1].getTranslationX() - event.getX();
                    } else if (event.getX() - mXFlag >= Config.touchSlop) {
                        mScrollTarget = SCROLL_LAST;
                        mXFlag = mItems[0].getTranslationX() - event.getX();
                    }
                } else if (mScrollTarget == SCROLL_CURR) {
                    mItems[1].setTranslationX(mXFlag + event.getX());
                } else if (mScrollTarget == SCROLL_LAST) {
                    mItems[0].setTranslationX(mXFlag + event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchFlag = false;

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity();

                if (mScrollTarget == SCROLL_CURR) {
                    mAnimator.setTarget(mItems[1]);
                    if (Math.abs(xv) < Config.flingVelocity) {
                        if (mItems[1].getTranslationX() > -getWidth() / 2) {
                            mAnimator.setFloatValues(0);
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[1].getTranslationX())));
                        } else {
                            mAnimator.setFloatValues(-getWidth());
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[1].getTranslationX() + getWidth())));
                        }
                    } else {
                        if (xv > 0) {
                            mAnimator.setFloatValues(0);
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[1].getTranslationX())));
                        } else {
                            mAnimator.setFloatValues(-getWidth());
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[1].getTranslationX() + getWidth())));
                        }
                    }
                    mAnimator.start();
                } else if (mScrollTarget == SCROLL_LAST) {
                    mAnimator.setTarget(mItems[0]);
                    if (Math.abs(xv) < Config.flingVelocity) {
                        if (mItems[0].getTranslationX() > -getWidth() / 2) {
                            mAnimator.setFloatValues(0);
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[0].getTranslationX())));
                        } else {
                            mAnimator.setFloatValues(-getWidth());
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[0].getTranslationX() + getWidth())));
                        }
                    } else {
                        if (xv > 0) {
                            mAnimator.setFloatValues(0);
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[0].getTranslationX())));
                        } else {
                            mAnimator.setFloatValues(-getWidth());
                            mAnimator.setDuration(Utils.getDuration(Math.abs(mItems[0].getTranslationX() + getWidth())));
                        }
                    }
                    mAnimator.start();
                }

                mVelocityTracker.recycle();
                break;
            default:
                break;
        }
        return true;
    }
}
