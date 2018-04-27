package me.gavin.app.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.DecelerateInterpolator;

import me.gavin.app.Config;
import me.gavin.app.model.Book;
import me.gavin.app.model.Word;
import me.gavin.base.App;
import me.gavin.text.R;

/**
 * 覆盖 - 翻页
 *
 * @author gavin.xiong 2018/4/22.
 */
public class CoverFlipper extends Flipper {

    private boolean mTouching; // 触摸中
    private boolean mDragging; // 拖动中
    private float mOffsetX; // 当前偏移量
    private float mTargetX; // 目标偏移量

    private VelocityTracker mVelocityTracker; // 滑动速度跟踪器
    private ValueAnimator mAnimator; // 翻页动画

    private final PointF mPoint = new PointF();

    private Bitmap[] bs = new Bitmap[3];

    private Drawable drawable;

    public CoverFlipper(Book book) {
        mPager = new LocalPager(book);

        drawable = App.get().getResources().getDrawable(R.drawable.bg_gradient, null);

        mAnimator = new ValueAnimator();
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mOffsetX = (float) animation.getAnimatedValue();
            mView.invalidate();
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mTouching && mOffsetX == mTargetX) {
                    if (mOffsetX == -Config.width && mPager.mPages[2] != null) {
                        mPager.nextPage();
                        toBitmap();
                        if (onPageChangeCallback != null) {
                            onPageChangeCallback.accept(mPager.mPages[1]);
                        }
                    } else if (mOffsetX == Config.width && mPager.mPages[0] != null) {
                        mPager.lastPage();
                        toBitmap();
                        if (onPageChangeCallback != null) {
                            onPageChangeCallback.accept(mPager.mPages[1]);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void offset(Long offset) {
        mPager.offset(offset);
    }

    @Override
    public void onPageReady() {
        toBitmap();
        if (mView != null) {
            mView.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouching = true;
                mPoint.set(event.getX(), event.getY());
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);

                if (mAnimator != null && mAnimator.isRunning()) {
                    mPoint.offset(-mOffsetX, 0);
                    mAnimator.cancel();
                    mDragging = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                if (!mDragging && Math.abs(event.getX() - mPoint.x) > Config.touchSlop) {
                    mDragging = true;
                    mPoint.set(event.getX(), event.getY());
                }
                if (mDragging) {
                    mOffsetX = event.getX() - mPoint.x;
                    mView.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouching = false;
                mDragging = false;
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity();
                if (Math.abs(xv) > Config.flingVelocity) { // 抛动
                    if (mOffsetX > 0) {
                        mTargetX = xv < 0 || mPager.mPages[0] == null ? 0 : Config.width;
                    } else {
                        mTargetX = xv > 0 || mPager.mPages[2] == null ? 0 : -Config.width;
                    }
                } else { // 静置松手
                    if (mOffsetX > 0) {
                        mTargetX = mOffsetX < Config.width / 2 || mPager.mPages[0] == null ? 0 : Config.width;
                    } else {
                        mTargetX = mOffsetX > -Config.width / 2 || mPager.mPages[2] == null ? 0 : -Config.width;
                    }
                }
                mAnimator.setFloatValues(mOffsetX, mTargetX);
                mAnimator.setDuration(Math.round(Math.abs(mTargetX - mOffsetX) * Config.flipAnimDuration));
                mAnimator.start();
                mVelocityTracker.recycle();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mOffsetX < 0) { // 左滑 - next
            canvas.drawBitmap(bs[2], 0, 0, Config.bgPaint);
        }
        canvas.drawBitmap(bs[1], Math.min(0, mOffsetX), 0, Config.bgPaint);
        if (mOffsetX > 0) { // 右滑 - last
            canvas.drawBitmap(bs[0], Math.min(0, mOffsetX - Config.width), 0, Config.bgPaint);
        }

        if (mOffsetX != 0) {
            float left = mOffsetX > 0 ? mOffsetX : (mOffsetX + Config.width);
            drawable.setBounds((int) left, 0, (int) left + 40, Config.height);
            drawable.draw(canvas);
        }
        canvas.drawText(mPager.mPages[1].start + "~" + mPager.mPages[1].end, 10, 40, Config.textPaint);
    }

    private void toBitmap() {
        for (int i = 0; i < mPager.mPages.length; i++) {
            bs[i] = Bitmap.createBitmap(Config.width, Config.height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bs[i]);
            canvas.drawRect(0, 0, Config.width, Config.height, Config.bgPaint);
            if (mPager.mPages[i] != null) {
                for (Word word : mPager.mPages[i].wordList) {
                    word.draw(canvas, 0, 0);
                }
            }
        }
        mOffsetX = 0;
        if (mView != null) {
            mView.invalidate();
        }
    }
}
