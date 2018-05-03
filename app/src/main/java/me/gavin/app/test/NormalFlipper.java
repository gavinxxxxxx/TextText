package me.gavin.app.test;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import me.gavin.app.Config;
import me.gavin.app.model.Word;

/**
 * 无 - 翻页
 *
 * @author gavin.xiong 2018/5/3
 */
public class NormalFlipper extends Flipper {

    private VelocityTracker mVelocityTracker; // 滑动速度跟踪器

    @Override
    public void flip(boolean reserve) {

    }

    @Override
    public void notifyPageChanged() {
        mView.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity();
                if (Math.abs(xv) > Config.flingVelocity) { // 抛动

                } else { // 静置松手
                    if (event.getX() < Config.width / 2) {
                        mView.onFlip(true);
                        mView.invalidate();
                        mView.onFlipped();
                    } else {
                        mView.onFlip(false);
                        mView.invalidate();
                        mView.onFlipped();
                    }
                }
                mVelocityTracker.recycle();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, Config.width, Config.height, Config.bgPaint);
        if (mView.curr() != null && mView.curr().ready) {
            for (Word word : mView.curr().wordList) {
                word.draw(canvas, 0, 0);
            }
            canvas.drawText(mView.curr().start + "~" + mView.curr().end, 10, 40, Config.textPaint);
        }
    }
}
