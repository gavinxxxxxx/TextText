package me.gavin.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 这里是萌萌哒注释菌
 * {@link android.support.v7.widget.FastScroller}
 *
 * @author gavin.xiong 2018/1/20.
 */
public class FastScrollLayout extends FrameLayout {

    private final Paint mTP;
    private final Paint mThP;

    private RecyclerView recycler;

    public FastScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setFocusableInTouchMode(true);
        setBackgroundColor(0);
        mTP = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTP.setColor(0x40000000);
        mThP = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThP.setColor(0x80FF0000);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return true;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        L.e("onTouchEvent");
//        return true;
//    }

//    @Override
//    public void onDrawForeground(Canvas canvas) {
//        super.onDrawForeground(canvas);
//        if (recycler == null) {
//            recycler = (RecyclerView) getChildAt(0);
//        }
//
//
//
//        canvas.drawRect(getWidth() - 32, 0, getWidth(), getHeight(), mTP);
//
//        canvas.drawRect(getWidth() - 32, 0, getWidth(), 100, mThP);
//
//    }
}
