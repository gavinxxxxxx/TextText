package me.gavin.app.test;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import me.gavin.app.Config;
import me.gavin.app.model.Line;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public class CoverFlipper extends Flipper {

    private final PointF mPoint = new PointF();
    private float mOffsetX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mOffsetX = event.getX() - mPoint.x;
                mView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPages[1] == null) return;

        for (Line line : mPages[1].lineList) {
            drawLine(canvas, line, mOffsetX, 0);
        }

        canvas.drawText(mPages[1].pageStart + "~" + mPages[1].pageEnd, 10, 40, Config.textPaint);
    }
}
