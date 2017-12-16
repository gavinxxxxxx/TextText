package me.gavin.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this.mTextSp = text == null ? null : text.split(mVm.REGEX_SEGMENT);
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
            while (start < segment.length() && y < getHeight()) {
                String remaining = segment.substring(start, segment.length());
                int count = breakText(remaining, start == 0);
                String line = segment.substring(start, start + Math.abs(count));
                drawLine(canvas, line.trim() + (count > 0 ? "" : "-"), y,
                        start == 0, start + Math.abs(count) >= segment.length());
                y = y + mVm.textHeight + mVm.lineSpacing;
                start += Math.abs(count);
            }
            // 段间距
            y += mVm.segmentSpacing;
        }
    }

    private int breakText(String remaining, boolean isFirstLine) {
        int count = mTextPaint.breakText(remaining, true, getWidth()
                - mVm.leftPadding - mVm.rightPadding - (isFirstLine ? mVm.indent : 0), null);
        return count >= remaining.length() ? count : countReset(remaining, count, isFirstLine);
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     *
     * @return count 数量 负数代表要加 -
     */
    private int countReset(String remaining, int count, boolean isFirstLine) {
        if (remaining.substring(count, count + 1).matches(mVm.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
            if (!remaining.substring(count - 2, count)
                    .matches(mVm.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
                count -= 1;
                return countReset(remaining, count, isFirstLine);
            }
        } else if (remaining.substring(count, count + 1).matches(mVm.REGEX_WORD)) { // 下一行第一个是字母或单词
            String line = remaining.substring(0, count);
            if (line.substring(count - 1, count).matches(mVm.REGEX_WORD)) { // 上一行以字母或单词结尾
                Pattern pattern = Pattern.compile(mVm.REGEX_WORD3);
                Matcher matcher = pattern.matcher(line);
                int groupCount = 0;
                String group = "";
                while (matcher.find()) {
                    groupCount++;
                    group = matcher.group();
                }
                int end = line.lastIndexOf(group);

                float indent = isFirstLine ? mVm.indent : 0;
                float textWidth = mTextPaint.measureText(line.substring(0, end));
                float lineWidth = getWidth() - mVm.leftPadding - mVm.rightPadding - indent;
                float extraSpace = lineWidth - textWidth; // 剩余空间

                float spacing = extraSpace / (groupCount > 1 ? groupCount - 1 : 1);
                if (spacing > mVm.wordSpacingMax) { // 单词间距过大 - 改为 abc- 形式
                    return 1 - count;
                }
                return end;
            }
        }
        return count;
    }

    /**
     * 显示文字
     *
     * @param canvas      画布
     * @param line        单行文字
     * @param y           当前行y坐标
     * @param isFirstLine 段落首行 - 首行缩进
     * @param isLastLine  段落末行 - 分散对齐
     */
    private void drawLine(Canvas canvas, String line, float y, boolean isFirstLine, boolean isLastLine) {
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
                Pattern pattern = Pattern.compile(mVm.REGEX_WORD3);
                Matcher matcher = pattern.matcher(line);
                int workCount = 0;
                while (matcher.find()) {
                    workCount++;
                }
                if (workCount > 1) {
                    float workSpacing = extraSpace / (workCount - 1);
                    float startX = mVm.leftPadding + indent;
                    float x;
                    StringBuilder sb = new StringBuilder();
                    int spacingCount = 0;
                    matcher.reset();
                    while (matcher.find()) {
                        String word = matcher.group();
                        x = startX + mTextPaint.measureText(sb.toString()) + workSpacing * spacingCount;
                        canvas.drawText(word, x, y, mTextPaint);
                        sb.append(word);
                        spacingCount++;
                    }
                } else {
                    float workSpacing = extraSpace / (line.length() - 1);
                    float startX = mVm.leftPadding + indent;
                    float x;
                    for (int i = 0; i < line.length(); i++) {
                        String word = String.valueOf(line.charAt(i));
                        x = startX + mTextPaint.measureText(line.substring(0, i)) + workSpacing * i;
                        canvas.drawText(word, x, y, mTextPaint);
                    }
                }
            }
        }
    }
}
