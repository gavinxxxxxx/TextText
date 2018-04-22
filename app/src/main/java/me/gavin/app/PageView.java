package me.gavin.app;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.model.Line;
import me.gavin.app.model.Page;

/**
 * TextTextView
 *
 * @author gavin.xiong 2017/12/13
 */
public class PageView extends View {

    private Page mPage;

    public PageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setKeepScreenOn(true);
    }

    public void set(Page page) {
        this.mPage = page;
        invalidate();
    }

    public boolean isNull() {
        return mPage == null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPage == null) return;

//        canvas.drawColor(Config.debugPaint.getColor());
//        canvas.drawRect(Config.leftPadding, Config.topPadding, getWidth() - Config.rightPadding,
//                getHeight() - Config.bottomPadding, Config.debugPaint);

        for (Line line : mPage.lineList) {
            Utils.drawLine(canvas, line);
        }

        canvas.drawText(mPage.pageStart + "~" + mPage.pageEnd, 10, 40, Config.textPaint);
    }
}
