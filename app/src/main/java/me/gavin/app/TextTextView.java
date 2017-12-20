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

        for (Line line : mPage.lineList) {
            drawLine(canvas, line);
        }
    }

    /**
     * 显示文字
     *
     * @param canvas 画布
     * @param line   单行文字 & 当前行y坐标 & 缩进 & 分散对齐
     */
    private void drawLine(Canvas canvas, Line line) {
        float indent = line.lineIndent ? Config.indent : 0;
        if (line.lineAlign || line.text.length() <= 1) { // 不需要分散对齐 | 只有一个字符
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
