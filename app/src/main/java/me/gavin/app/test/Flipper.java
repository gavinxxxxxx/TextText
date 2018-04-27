package me.gavin.app.test;

import android.graphics.Canvas;
import android.view.MotionEvent;

import me.gavin.app.model.Page;
import me.gavin.base.function.Consumer;

/**
 * 翻页器
 *
 * @author gavin.xiong 2018/4/22.
 */
public abstract class Flipper {

    TextView mView;

    Pager mPager;

    Consumer<Page> onPageChangeCallback;

    public void attach(TextView view) {
        this.mView = view;
        view.setFlipper(this);
    }

    public void set() {
        mPager.offset(mPager.mBook.getOffset());
        if (mView != null) {
            mView.invalidate();
        }
    }

    public void setOnPageChangeCallback(Consumer<Page> callback) {
        this.onPageChangeCallback = callback;
    }

    public abstract void offset(Long offset);

    public abstract void onPageReady();

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);
}
