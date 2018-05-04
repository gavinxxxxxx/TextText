package me.gavin.app.core.flipper;

import android.graphics.Canvas;
import android.text.StaticLayout;
import android.view.MotionEvent;

import me.gavin.app.core.TextView;

/**
 * 翻页器
 *
 * @author gavin.xiong 2018/4/22.
 */
public abstract class Flipper {

    public TextView mView;

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);

}
