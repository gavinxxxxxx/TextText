package me.gavin.app.core.flipper;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import me.gavin.app.Config;
import me.gavin.app.core.model.Word;
import me.gavin.base.App;
import me.gavin.text.R;

/**
 * 无 - 翻页
 *
 * @author gavin.xiong 2018/5/3
 */
public class SimpleFlipper extends Flipper {

    private Drawable drawable;

    public SimpleFlipper() {
        drawable = App.get().getDrawable(R.drawable.anim_loading);
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
//            canvas.drawPath(mView.curr().path, Config.textPaint);
//            for (Path path : mView.curr().paths) {
//                canvas.drawPath(path, Config.textPaint);
//            }
            canvas.drawText(mView.curr().start + "~" + mView.curr().end, 10, 40, Config.textPaint);
        } else {
            canvas.drawText("加载中...", Config.width / 2, Config.height / 2, Config.textPaint);
        }
    }
}
