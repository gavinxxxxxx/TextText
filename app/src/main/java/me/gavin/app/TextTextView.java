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
                int count = mTextPaint.breakText(remaining, true,
                        getWidth() - mVm.leftPadding - mVm.rightPadding - (start == 0 ? mVm.indent : 0), null);

                count = countRest(segment, start, count);

                String line = segment.substring(start, start + count);
                if (start + count < segment.length() && segment.substring(start, start + count + 1).matches("[A-Za-z0-9\\-]+")) {
                    line = line.substring(0, line.length() - 1) + "-";
                    count--;
                }
                boolean a = dratLine(canvas, line.trim(), y, start == 0, start + count >= segment.length());
                if (!a) {
                    count = mTextPaint.breakText(remaining, true,
                            getWidth() - mVm.leftPadding - mVm.rightPadding - (start == 0 ? mVm.indent : 0), null) - 1;
                    line = segment.substring(start, start + count) + "-";
                    dratLine(canvas, line.trim(), y, start == 0, start + count >= segment.length());
                }
                y = y + mVm.textHeight + mVm.lineSpacing;
                start += count;
            }
            // 段间距
            y += mVm.segmentSpacing;
        }
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     */
    private int countRest(String segment, int start, int count) {
        if (start + count < segment.length()) { // 不是最后一行
            if (segment.substring(start + count, start + count + 1)
                    .matches(mVm.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
                if (!segment.substring(start + count - 2, start + count + 1)
                        .matches(mVm.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
                    count -= 1;
                    return countRest(segment, start, count);
                }
            } else if (segment.substring(start + count, start + count + 1).matches("[A-Za-z0-9\\-]") // 下一行第一个是字母或单词
                    && !segment.substring(start, start + count).matches("[A-Za-z0-9\\-]+")) { // && 不是一整行全是字母或单词
                if (segment.substring(start + count - 1, start + count)
                        .matches("[A-Za-z0-9\\-]")) { // 上一行最后一个也是字母或单词
                    count -= 1;
                    return countRest(segment, start, count);
                }
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
    private boolean dratLine(Canvas canvas, String line, float y, boolean isFirstLine, boolean isLastLine) {
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
                Pattern pattern = Pattern.compile("([A-Za-z0-9\\-]+|[^A-Za-z0-9\\-])");
                Matcher matcher = pattern.matcher(line);
                int workCount = 0;
                while (matcher.find()) {
                    workCount++;
                }

                float spacing = extraSpace / (workCount > 1 ? workCount - 1 : 1);
                if (spacing > mVm.wordSpacingMax) { // 单词间距过大 - 改为 x- 形式
                    return false;
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
        return true;
    }
}
