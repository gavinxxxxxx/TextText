package me.gavin.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TextTextView
 *
 * @author gavin.xiong 2017/12/13
 */
public class TextTextView extends View {

    private final ViewModel mVm;

    private String mText = "哈喽";
    private String[] mTextSp;

    private final TextPaint mTextPaint;
    private final Paint mDebugPaint;

    public TextTextView(Context context) {
        this(context, null);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVm = new ViewModel();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mVm.textSize);
        mTextPaint.setColor(mVm.textColor);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mVm.textHeight = fontMetrics.bottom - fontMetrics.top;

        mDebugPaint = new Paint();
        mDebugPaint.setColor(0x22222222);
    }

    public void setText(String text) {
        this.mText = text;
        this.mTextSp = text == null ? null : text.split(mVm.regex);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() <= 0 || mTextSp == null) return;

        canvas.drawRect(mVm.leftPadding, mVm.topPadding, getWidth() - mVm.rightPadding,
                getHeight() - mVm.bottomPadding, mDebugPaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        float y = mVm.topPadding + mVm.textHeight;
        for (String segment : mTextSp) {
            int start = 0;
            while (start < segment.length()) {
                String line = segment.substring(start, segment.length());
                int count = mTextPaint.breakText(line, true,
                        getWidth() - mVm.leftPadding - mVm.rightPadding - (start == 0 ? mVm.indent : 0), null);
                line = segment.substring(start, start + count);
                dratLine(canvas, line, y, start == 0, start + count >= segment.length());
                y = y + mVm.textHeight + mVm.lineSpacing;
                start += count;
            }
            // 段间距
            y += mVm.segmentSpacing;
        }
    }

    /**
     * 显示文字
     * todo 英文短句 & 标点
     *
     * @param canvas      画布
     * @param line        单行文字
     * @param y           当前行y坐标
     * @param isFirstLine 段落首行 - 首行缩进
     * @param isLastLine  段落末行 - 分散对齐
     */
    private void dratLine(Canvas canvas, String line, float y, boolean isFirstLine, boolean isLastLine) {
        float indent = isFirstLine ? mVm.indent : 0;
        if (isLastLine) { // 最后一行不需要分散对齐
            canvas.drawText(line, mVm.leftPadding + indent, y, mTextPaint);
        } else {
            float textWidth = mTextPaint.measureText(line);
            float lineWidth = getWidth() - mVm.leftPadding - mVm.rightPadding - indent;
            float extraSpace = lineWidth - textWidth; // 剩余空间
            if (extraSpace <= 0) { // 没有多余空间
                canvas.drawText(line, mVm.leftPadding + indent, y, mTextPaint);
            } else {
                float textSpacing = extraSpace / (line.length() - 1);
                float startX = mVm.leftPadding + indent;
                float x = startX;
                for (int i = 0; i < line.length(); i++) {
                    String word = String.valueOf(line.charAt(i));
                    canvas.drawText(word, x, y, mTextPaint);
                    x = startX + mTextPaint.measureText(line.substring(0, i + 1)) + textSpacing * i;
                }
            }
        }
    }
}
