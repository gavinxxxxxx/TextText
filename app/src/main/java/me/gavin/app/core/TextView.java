package me.gavin.app.core;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.Config;
import me.gavin.app.core.flipper.Flipper;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.pager.Pager;

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
        int ufs = SYSTEM_UI_FLAG_LAYOUT_STABLE
                | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (ColorUtils.calculateLuminance(Config.bgColor) > 0.5 && Build.VERSION.SDK_INT >= 23) {
            ufs |= SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        setSystemUiVisibility(ufs);
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

    public void set(Page page) {
        pages.clear();
        pages.add(page);
        index = 0;
        invalidate();
        preload();
    }

    public void update(Page page) {
        invalidate();
        preload();
    }

    public void preload() {
        if (curr().ready && index == 0 && !curr().isFirst) {
            Page last = new Page(curr().book);
            pages.add(0, last);
            index++;
            mPager.last(curr(), last);
        }
        if (curr().ready && index == pages.size() - 1 && !curr().isLast) {
            Page next = new Page(curr().book);
            pages.add(next);
            mPager.next(curr(), next);
        }
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
        invalidate();
        mPager.onFlip(curr());
        preload();
    }

    public void onTap() {
        toggleFull(null);
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

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleFull(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        toggleFull(hasWindowFocus);
    }

    public void toggleFull(Boolean full) {
        int target = SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        int flags = getSystemUiVisibility();
        flags = full == null ? flags ^ target : full ? flags | target : flags ^ (flags & target);
        setSystemUiVisibility(flags);
    }
}
