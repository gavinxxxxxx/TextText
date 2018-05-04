package me.gavin.app.core.flipper;

import android.graphics.Canvas;
import android.view.MotionEvent;

import me.gavin.app.Config;
import me.gavin.app.core.model.Word;

/**
 * 无 - 翻页
 *
 * @author gavin.xiong 2018/5/3
 */
public class SimpleFlipper extends Flipper {

    @Override
    public void flip(boolean reserve) {

    }

    @Override
    public void notifyPageChanged() {
        mView.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() < Config.width / 2 && mView.last() != null) {
                mView.onFlip(true);
            } else if (event.getX() > Config.width / 2 && mView.next() != null) {
                mView.onFlip(false);
            }
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
