package me.gavin.app.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import me.gavin.app.Config;
import me.gavin.app.Utils;
import me.gavin.app.model.Line;
import me.gavin.app.model.Page;

/**
 * TextView
 *
 * @author gavin.xiong 2018/4/22.
 */
public class TextView extends View {

    private final Page[] mPages = new Page[Config.pageCount];

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setKeepScreenOn(true);
    }

    public void set(Page last, Page curr, Page next) {
        mPages[0] = last;
        mPages[1] = curr;
        mPages[2] = next;
        invalidate();
    }

    private final PointF mPoint = new PointF();
    private float mXFlag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPoint.set(event.getX(), event.getY());
                mXFlag = getTranslationX() - event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                setTranslationX(mXFlag + event.getX());
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPages[1] == null) return;

        for (Line line : mPages[1].lineList) {
            Utils.drawLine(canvas, line);
        }

        canvas.drawText(mPages[1].pageStart + "~" + mPages[1].pageEnd, 10, 40, Config.textPaint);
    }
}
