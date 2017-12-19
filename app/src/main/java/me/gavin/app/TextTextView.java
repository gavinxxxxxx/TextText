package me.gavin.app;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.model.Line;
import me.gavin.app.model.Page;

/**
 * TextTextView
 *
 * @author gavin.xiong 2017/12/13
 */
public class TextTextView extends View {

    private Page mPage;

    public TextTextView(Context context) {
        this(context, null);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void set(Page page) {
        this.mPage = page;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPage == null) return;

        canvas.drawColor(Config.debugPaint.getColor());
        canvas.drawRect(Config.leftPadding, Config.topPadding, getWidth() - Config.rightPadding,
                getHeight() - Config.bottomPadding, Config.debugPaint);

        layoutAndDrawText(canvas);
    }

    private List<Line> lineList = new ArrayList<>();

    private void layoutAndDrawText(Canvas canvas) {
        lineList.clear();

        float y = Config.topPadding - Config.textTop;
        String subText = mPage.mText; // 子字符串 - 计算字符数量
        for (int i = 0; i < mPage.mTextSp.length; i++) {
            String segment = mPage.mTextSp[i];
            int start = 0;
            while (start < segment.length()) {
                String remaining = segment.substring(start);
                if (!mPage.reverseFlag && y + Config.textBottom > getHeight() - Config.bottomPadding) {
                    mPage.pageLimit = mPage.mText.indexOf(subText); // 计算字符数量
                    mPage.pageEnd = mPage.pageStart + mPage.pageLimit;
                    mPage.isLast = mPage.pageEnd >= mPage.book.getLength();
                    return;
                }

                boolean lineIndent = i == 0 && start == 0 && mPage.firstLineIndent
                        || i != 0 && start == 0;
                int count = breakText(remaining, lineIndent);
                boolean lineAlign = start + Math.abs(count) >= segment.length()
                        && !(mPage.isReverse && mPage.lastLineAlign && i == mPage.mTextSp.length - 1);

                String text = segment.substring(start, start + Math.abs(count));
                Line line = new Line(text.trim() + (count > 0 ? "" : "-"), y, lineIndent, lineAlign);
                lineList.add(line);

                if (!mPage.reverseFlag) {
                    drawLine(canvas, line);
                }

                y = y + Config.textHeight + Config.lineSpacing; // TODO: 2017/12/18 *? | 分段没分好
                start += Math.abs(count);
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            // 段间距
            y += Config.segmentSpacing;
        }
        if (mPage.reverseFlag) {
            float maxY = y - Config.segmentSpacing - Config.lineSpacing; // y 最大值
            float extraY = maxY - getHeight() + Config.bottomPadding - Config.textTop;
            subText = mPage.mText; // 子字符串 - 计算字符数量
            for (Line line : lineList) {
                if (line.y - 2 < extraY) { // TODO: 2017/12/18 *? -> line
                    subText = subText.substring(subText.indexOf(line.text) + line.text.length());
                } else {
                    break;
                }
            }
            mPage.pageLimit = subText.length();
            mPage.pageStart = mPage.pageEnd - mPage.pageLimit;
            mPage.isFirst = mPage.pageStart <= 0;
            mPage.mText = Utils.trim(mPage.mText.substring(mPage.mText.length() - mPage.pageLimit));
            mPage.mTextSp = mPage.mText.split(Config.REGEX_SEGMENT);
            mPage.reverseFlag = false;
            layoutAndDrawText(canvas);
        }
    }

    private int breakText(String remaining, boolean lineIndent) {
        int count = Config.textPaint.breakText(remaining, true, getWidth()
                - Config.leftPadding - Config.rightPadding - (lineIndent ? Config.indent : 0), null);
        return count >= remaining.length() ? count : countReset(remaining, count, lineIndent);
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     *
     * @return count 数量 负数代表要加 -
     */
    private int countReset(String remaining, int count, boolean lineIndent) {
        if (remaining.substring(count, count + 1).matches(Config.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
            if (!remaining.substring(count - 2, count)
                    .matches(Config.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
                count -= 1;
                return countReset(remaining, count, lineIndent);
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

            float indent = lineIndent ? Config.indent : 0;
            float textWidth = Config.textPaint.measureText(line.substring(0, end));
            float lineWidth = getWidth() - Config.leftPadding - Config.rightPadding - indent;
            float extraSpace = lineWidth - textWidth; // 剩余空间

            float spacing = extraSpace / (groupCount > 1 ? groupCount - 1 : 1);
            if (spacing > Config.wordSpacingMax) { // 单词间距过大 - 改为 abc- 形式
                return 1 - count;
            }
            return end;
        }
        return count;
    }

    /**
     * 显示文字
     *
     * @param canvas 画布
     * @param line   单行文字 & 当前行y坐标 & 缩进 & 分散对齐
     */
    private void drawLine(Canvas canvas, Line line) {
        float indent = line.lineIndent ? Config.indent : 0;
        if (line.lineAlign) { // 最后一行 - 不需要分散对齐
            canvas.drawText(line.text, Config.leftPadding + indent, line.y, Config.textPaint);
            return;
        }

        float textWidth = Config.textPaint.measureText(line.text);
        float lineWidth = getWidth() - Config.leftPadding - Config.rightPadding - indent;
        float extraSpace = lineWidth - textWidth; // 剩余空间
        if (extraSpace <= 0) { // 没有多余空间 - 不需要分散对齐
            canvas.drawText(line.text, Config.leftPadding + indent, line.y, Config.textPaint);
            return;
        }

        Matcher matcher = Pattern.compile(Config.REGEX_WORD3).matcher(line.text);
        int workCount = 0;
        while (matcher.find()) {
            workCount++;
        }
        if (workCount > 1) { // 多个单词 - 词间距
            float workSpacing = extraSpace / (workCount - 1);
            float startX = Config.leftPadding + indent;
            float x;
            StringBuilder sb = new StringBuilder();
            int spacingCount = 0;
            matcher.reset();
            while (matcher.find()) {
                String word = matcher.group();
                x = startX + Config.textPaint.measureText(sb.toString()) + workSpacing * spacingCount;
                canvas.drawText(word, x, line.y, Config.textPaint);
                sb.append(word);
                spacingCount++;
            }
        } else { // 单个单词 - 字间距
            float workSpacing = extraSpace / (line.text.length() - 1);
            float startX = Config.leftPadding + indent;
            float x;
            for (int i = 0; i < line.text.length(); i++) {
                String word = String.valueOf(line.text.charAt(i));
                x = startX + Config.textPaint.measureText(line.text.substring(0, i)) + workSpacing * i;
                canvas.drawText(word, x, line.y, Config.textPaint);
            }
        }
    }
}
