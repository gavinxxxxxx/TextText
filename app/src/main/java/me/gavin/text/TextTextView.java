package me.gavin.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TextTextView
 *
 * @author gavin.xiong 2017/12/13
 */
public class TextTextView extends View {

    private String mText = "哈喽";

    private TextPaint mPaint;
    StaticLayout staticLayout;

    public TextTextView(Context context) {
        this(context, null);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(40f);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && mText != null) {
            staticLayout = new StaticLayout(mText, mPaint, getWidth() - 100,
                    Layout.Alignment.ALIGN_NORMAL, 1.2f, 0.0f, false);
        }
    }

    public void setText(String text) {
        this.mText = text;
        if (getWidth() > 0) {
            staticLayout = new StaticLayout(mText, mPaint, getWidth() - 100,
                    Layout.Alignment.ALIGN_NORMAL, 1.2f, 0.0f, false);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: 2017/12/13  Paint.breakText

        if (getWidth() <= 0 || mText == null) return;
        canvas.save();
        canvas.translate(50, 0);
        staticLayout.draw(canvas);
        canvas.restore();
    }
}
