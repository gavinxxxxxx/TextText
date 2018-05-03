package me.gavin.app.test;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.Config;
import me.gavin.app.model.Page;

/**
 * TextView
 *
 * @author gavin.xiong 2018/4/22.
 */
public class TextView extends View {

    final List<Page> pages = new ArrayList<>();
    int index = -1;

    private Pager mPager;
    private Flipper mFlipper;

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setKeepScreenOn(true);
    }

    public Page curr() {
        return pages.isEmpty() ? null : pages.get(index);
    }

    public Page last() {
        return pages.isEmpty() || index == 0 ? null : pages.get(index - 1);
    }

    public Page next() {
        return pages.isEmpty() || index == pages.size() - 1 ? null : pages.get(index + 1);
    }

    public Page header() {
        return pages.isEmpty() ? null : pages.get(0);
    }

    public Page footer() {
        return pages.isEmpty() ? null : pages.get(pages.size() - 1);
    }

    public void set(Page page) {
        pages.clear();
        index = -1;
        add(page, true);
    }

    public void add(Page page, boolean header) {
        pages.add(header ? 0 : pages.size(), page);
        index = header ? index + 1 : index;
        mFlipper.notifyPageChanged();
        onFlipped();
    }

    public void update(Page page) {
        int i = pages.indexOf(page);
        mFlipper.notifyPageChanged();
        // TODO: 2018/4/29 更新视图
    }

    public void setPager(Pager pager) {
        this.mPager = pager;
        invalidate();
    }

    public void setFlipper(Flipper flipper) {
        this.mFlipper = flipper;
        flipper.mView = this;
        invalidate();
    }

    public void onFlip(boolean reserve) {
        if (reserve) {
            index--;
        } else {
            index++;
        }
    }

    public void onFlipped() {
        mPager.onFlipped(curr());
        if (index == 0 && !curr().isFirst) {
            mPager.last();
        }
        if (index == pages.size() - 1 && !curr().isLast) {
            mPager.next();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        if (w != ow || h != oh) {
            Config.applySizeChange(w, h);
            mPager.curr();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !pages.isEmpty() && mFlipper != null && mFlipper.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!pages.isEmpty() && mFlipper != null) {
            mFlipper.onDraw(canvas);
        }
    }
}
