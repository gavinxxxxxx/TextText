package me.gavin.app;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
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

    private String mText;
    private String[] mTextSp;

    public TextTextView(Context context) {
        this(context, null);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVm = new ViewModel();
    }

    public void setText(String text, long offset) {
        this.mText = text;
        this.mTextSp = text == null ? null : text.split(Config.REGEX_SEGMENT);
        this.mVm.pageOffset = offset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() <= 0 || mTextSp == null) return;

        canvas.drawColor(mVm.debugPaint.getColor());
        canvas.drawRect(mVm.leftPadding, mVm.topPadding, getWidth() - mVm.rightPadding,
                getHeight() - mVm.bottomPadding, mVm.debugPaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        float y = mVm.topPadding - mVm.textAscent;
        String subText = mText; // 子字符串 - 计算字符数量
        for (String segment : mTextSp) {
            int start = 0;
            while (start < segment.length()) {
                String remaining = segment.substring(start, segment.length());
                if (y + mVm.textDescent > getHeight() - mVm.bottomPadding) {
                    L.e(remaining);
                    L.e(subText);
                    mVm.pageLimit = mText.indexOf(subText); // 计算字符数量
                    return;
                }

                int count = breakText(remaining, start == 0);
                String line = segment.substring(start, start + Math.abs(count));
                drawLine(canvas, line.trim() + (count > 0 ? "" : "-"), y,
                        start == 0, start + Math.abs(count) >= segment.length());
                y = y + mVm.textHeight + mVm.lineSpacing;
                start += Math.abs(count);
                subText = subText.substring(subText.indexOf(line) + line.length(), subText.length());
            }
            // 段间距
            y += mVm.segmentSpacing;
        }
    }

    private int breakText(String remaining, boolean isFirstLine) {
        int count = mVm.textPaint.breakText(remaining, true, getWidth()
                - mVm.leftPadding - mVm.rightPadding - (isFirstLine ? mVm.indent : 0), null);
        return count >= remaining.length() ? count : countReset(remaining, count, isFirstLine);
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     *
     * @return count 数量 负数代表要加 -
     */
    private int countReset(String remaining, int count, boolean isFirstLine) {
        if (remaining.substring(count, count + 1).matches(Config.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
            if (!remaining.substring(count - 2, count)
                    .matches(Config.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
                count -= 1;
                return countReset(remaining, count, isFirstLine);
            }
        } else if (remaining.substring(count - 1, count + 1).matches(Config.REGEX_WORD2)) { // 上一行最后一个字符和下一行第一个字符符合单词形式
            String line = remaining.substring(0, count);
            Matcher matcher = Pattern.compile(Config.REGEX_WORD3).matcher(line);
            int groupCount = 0;
            String group = "";
            while (matcher.find()) {
                groupCount++;
                group = matcher.group();
            }
            int end = line.lastIndexOf(group);

            float indent = isFirstLine ? mVm.indent : 0;
            float textWidth = mVm.textPaint.measureText(line.substring(0, end));
            float lineWidth = getWidth() - mVm.leftPadding - mVm.rightPadding - indent;
            float extraSpace = lineWidth - textWidth; // 剩余空间

            float spacing = extraSpace / (groupCount > 1 ? groupCount - 1 : 1);
            if (spacing > mVm.wordSpacingMax) { // 单词间距过大 - 改为 abc- 形式
                return 1 - count;
            }
            return end;
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
        if (isLastLine) { // 最后一行 - 不需要分散对齐
            canvas.drawText(line, mVm.leftPadding + indent, y, mVm.textPaint);
            return;
        }

        float textWidth = mVm.textPaint.measureText(line);
        float lineWidth = getWidth() - mVm.leftPadding - mVm.rightPadding - indent;
        float extraSpace = lineWidth - textWidth; // 剩余空间
        if (extraSpace <= 0) { // 没有多余空间 - 不需要分散对齐
            canvas.drawText(line, mVm.leftPadding + indent, y, mVm.textPaint);
            return;
        }

        Matcher matcher = Pattern.compile(Config.REGEX_WORD3).matcher(line);
        int workCount = 0;
        while (matcher.find()) {
            workCount++;
        }
        if (workCount > 1) { // 多个单词 - 词间距
            float workSpacing = extraSpace / (workCount - 1);
            float startX = mVm.leftPadding + indent;
            float x;
            StringBuilder sb = new StringBuilder();
            int spacingCount = 0;
            matcher.reset();
            while (matcher.find()) {
                String word = matcher.group();
                x = startX + mVm.textPaint.measureText(sb.toString()) + workSpacing * spacingCount;
                canvas.drawText(word, x, y, mVm.textPaint);
                sb.append(word);
                spacingCount++;
            }
        } else { // 单个单词 - 字间距
            float workSpacing = extraSpace / (line.length() - 1);
            float startX = mVm.leftPadding + indent;
            float x;
            for (int i = 0; i < line.length(); i++) {
                String word = String.valueOf(line.charAt(i));
                x = startX + mVm.textPaint.measureText(line.substring(0, i)) + workSpacing * i;
                canvas.drawText(word, x, y, mVm.textPaint);
            }
        }
    }
}
