package me.gavin.app.test;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 翻页器
 *
 * @author gavin.xiong 2018/4/22.
 */
public abstract class Flipper {

    TextView mView;

    public abstract void flip(boolean reserve);

    public abstract void offset(Long offset);

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);
}
