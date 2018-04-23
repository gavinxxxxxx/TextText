package me.gavin.app.test;

import android.graphics.Canvas;
import android.view.MotionEvent;

import me.gavin.app.Config;
import me.gavin.app.model.Page;
import me.gavin.base.function.Consumer;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public abstract class Flipper {

    TextView mView;

    final Page[] mPages = new Page[Config.pageCount];

    Consumer<Boolean> onPageChangeCallback;

    public void attach(TextView view) {
        this.mView = view;
        view.setFlipper(this);
    }

    public void set(Page last, Page curr, Page next) {
        mPages[0] = last;
        mPages[1] = curr;
        mPages[2] = next;
        if (mView != null) {
            mView.invalidate();
        }
    }

    public void setOnPageChangeCallback(Consumer<Boolean> callback) {
        this.onPageChangeCallback = callback;
    }

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);
}
