package me.gavin.app.test;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TextView
 *
 * @author gavin.xiong 2018/4/22.
 */
public class TextView extends View {

    private Flipper mFlipper;

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setKeepScreenOn(true);
    }

    public void setFlipper(Flipper flipper) {
        this.mFlipper = flipper;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mFlipper != null && mFlipper.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFlipper != null) {
            mFlipper.onDraw(canvas);
        }
    }
}
