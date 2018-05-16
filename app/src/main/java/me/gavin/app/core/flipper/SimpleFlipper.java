package me.gavin.app.core.flipper;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import me.gavin.app.Config;
import me.gavin.app.core.model.Word;

/**
 * 无 - 翻页
 *
 * @author gavin.xiong 2018/5/3
 */
public class SimpleFlipper extends Flipper {

    private VelocityTracker mVelocityTracker; // 滑动速度跟踪器

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xv = mVelocityTracker.getXVelocity();
                if (Math.abs(xv) > Config.flingVelocity) { // 抛动
                    if (xv > 0 && mView.last() != null) {
                        mView.onFlip(true);
                    } else if (xv < 0 && mView.next() != null) {
                        mView.onFlip(false);
                    }
                } else { // 静置松手
                    if (event.getX() < Config.width * 0.33 && mView.last() != null) {
                        mView.onFlip(true);
                    } else if (event.getX() > Config.width * 0.67 && mView.next() != null) {
                        mView.onFlip(false);
                    } else if (event.getX() > Config.width * 0.33 && event.getX() < Config.width * 0.67) {
                        mView.onTap();
                    }
                }
                return true;
            default:
                mVelocityTracker.addMovement(event);
                return true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mView.curr() != null && mView.curr().ready) {
            for (Word word : mView.curr().wordList) {
                word.draw(canvas, 0, 0);
            }
            canvas.drawText(mView.curr().start + "~" + mView.curr().end, 10, 40, Config.textPaint);
        } else {
            String s = "加载中...";
            canvas.drawText(s, Config.width / 2 - Config.textPaint.measureText(s) / 2, Config.height / 2, Config.textPaint);
        }
    }
}
