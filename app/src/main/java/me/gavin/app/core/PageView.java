package me.gavin.app.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public class PageView extends View {

    private static final float RADIUS = 10f;

    private int mWidth, mHeight;
    private final PointF[] mPFs;
    private final Paint mPaint, pathBPaint, pathCPaint, mDebugPaint;
    private final Path mPath, pathB, mPathC;

    public PageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPFs = new PointF[11];
        for (int i = 0; i < mPFs.length; i++) {
            mPFs[i] = new PointF();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));

        pathBPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathBPaint.setColor(Color.BLUE);
        pathBPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        pathCPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathCPaint.setColor(Color.YELLOW);
        pathCPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        mDebugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDebugPaint.setColor(ContextCompat.getColor(context, android.R.color.holo_red_light));

        mPath = new Path();
        pathB = new Path();
        mPathC = new Path();


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mPathC, pathCPaint);
        canvas.drawPath(pathB, pathBPaint);


        for (PointF t : mPFs) {
            canvas.drawCircle(t.x, t.y, RADIUS, mDebugPaint);
        }
    }

    PointF pointF;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointF = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                calcPoint(event.getX(), event.getY());
//                calcPoint(mWidth + event.getX() - pointF.x, mHeight + event.getY() - pointF.y);
//                resetPath();
                resetPath2();
                getPathB();
                getPathC();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    private void calcPoint(float x, float y) {
        mPFs[0].set(x, y); // a
        mPFs[5].set(mWidth, y < mHeight ? mHeight : 0); // f
        mPFs[6].set((mPFs[0].x + mPFs[5].x) / 2, (mPFs[0].y + mPFs[5].y) / 2); // g

        mPFs[4].set(mPFs[6].x - (mPFs[5].y - mPFs[6].y) * (mPFs[5].y - mPFs[6].y) / (mPFs[5].x - mPFs[6].x), mPFs[5].y); // e
        mPFs[7].set(mPFs[5].x, mPFs[6].y - (mPFs[5].x - mPFs[6].x) * (mPFs[5].x - mPFs[6].x) / (mPFs[5].y - mPFs[6].y)); // h

        mPFs[2].set(mPFs[4].x - (mPFs[5].x - mPFs[4].x) / 2, mPFs[5].y); // c
        mPFs[9].set(mPFs[5].x, mPFs[7].y - (mPFs[5].y - mPFs[7].y) / 2); // j

        mPFs[1].set(getIntersectionPoint(mPFs[0], mPFs[4], mPFs[2], mPFs[9])); // b
        mPFs[10].set(getIntersectionPoint(mPFs[0], mPFs[7], mPFs[2], mPFs[9])); // k

        mPFs[3].set((mPFs[2].x + mPFs[4].x * 2 + mPFs[1].x) / 4, (mPFs[4].y * 2 + mPFs[2].y + mPFs[1].y) / 4); // d
        mPFs[8].set((mPFs[9].x + mPFs[7].x * 2 + mPFs[10].x) / 4, (mPFs[7].y * 2 + mPFs[9].y + mPFs[10].y) / 4); // i
    }

    private void resetPath() {
        mPath.reset();
        mPath.lineTo(0, mHeight); //移动到左下角
        mPath.lineTo(mPFs[2].x, mPFs[2].y);//移动到c点
        mPath.quadTo(mPFs[4].x, mPFs[4].y, mPFs[1].x, mPFs[1].y);//从c到b画贝塞尔曲线，控制点为e
        mPath.lineTo(mPFs[0].x, mPFs[0].y);//移动到a点
        mPath.lineTo(mPFs[10].x, mPFs[10].y);//移动到k点
        mPath.quadTo(mPFs[7].x, mPFs[7].y, mPFs[9].x, mPFs[9].y);//从k到j画贝塞尔曲线，控制点为h
        mPath.lineTo(mWidth, 0);//移动到右上角
        mPath.close();
    }

    private void resetPath2() {
        mPath.reset();
        mPath.moveTo(mPFs[5].x, mPFs[5].y); // 移动到f点
        mPath.lineTo(mPFs[2].x, mPFs[2].y);//移动到c点
        mPath.quadTo(mPFs[4].x, mPFs[4].y, mPFs[1].x, mPFs[1].y);//从c到b画贝塞尔曲线，控制点为e
        mPath.lineTo(mPFs[0].x, mPFs[0].y);//移动到a点
        mPath.lineTo(mPFs[10].x, mPFs[10].y);//移动到k点
        mPath.quadTo(mPFs[7].x, mPFs[7].y, mPFs[9].x, mPFs[9].y);//从k到j画贝塞尔曲线，控制点为h
        mPath.close();
        Path path = new Path();
        path.lineTo(0, mHeight);
        path.lineTo(mWidth, mHeight);
        path.lineTo(mWidth, 0);
        path.close();
        mPath.op(path, Path.Op.REVERSE_DIFFERENCE);
    }

    /**
     * 绘制区域C
     */
    private void getPathC() {
        mPathC.reset();
        mPathC.moveTo(mPFs[8].x, mPFs[8].y);//移动到i点
        mPathC.lineTo(mPFs[3].x, mPFs[3].y);//移动到d点
        mPathC.lineTo(mPFs[1].x, mPFs[1].y);//移动到b点
        mPathC.lineTo(mPFs[0].x, mPFs[0].y);//移动到a点
        mPathC.lineTo(mPFs[10].x, mPFs[10].y);//移动到k点
        mPathC.close();//闭合区域
    }
    /**
     * 绘制区域B
     * @return
     */
    private void getPathB(){
        pathB.reset();
        pathB.lineTo(0, mHeight);//移动到左下角
        pathB.lineTo(mWidth,mHeight);//移动到右下角
        pathB.lineTo(mWidth,0);//移动到右上角
        pathB.close();//闭合区域
    }

    /**
     * 计算两线段相交点坐标
     */
    private PointF getIntersectionPoint(PointF pa, PointF pb, PointF px, PointF py) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = pa.x;
        y1 = pa.y;
        x2 = pb.x;
        y2 = pb.y;
        x3 = px.x;
        y3 = px.y;
        x4 = py.x;
        y4 = py.y;

        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return new PointF(pointX, pointY);
    }
}
